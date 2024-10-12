package com.fumbbl.ffb.client;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.BloodSpot;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.WeatherFactory;
import com.fumbbl.ffb.json.JsonStringMapOption;
import com.fumbbl.ffb.model.BlockKind;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilUrl;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Kalimar
 */
public class IconCache {

	private static final Pattern _PATTERN_PITCH = Pattern.compile("\\?pitch=([a-z]+)$");
	private static final String LOCAL_CACHE_MAP_FILE = "map.json";
	private static final JsonStringMapOption JSON_OPTION = new JsonStringMapOption("map");
	private final Map<String, BufferedImage> fIconByKey;
	private final Map<String, BufferedImage> scaledIcons;

	private Properties fIconUrlProperties;

	private final Map<String, Integer> fCurrentIndexPerKey;

	private final FantasyFootballClient fClient;
	private final DimensionProvider dimensionProvider;
	private final Map<String, String> localCacheMap = new HashMap<>();
	private MessageDigest digest;
	private String localCacheFolder;
	private final HttpClient httpClient;

	public IconCache(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		fClient = pClient;
		this.dimensionProvider = dimensionProvider;
		fIconByKey = new HashMap<>();
		scaledIcons = new HashMap<>();
		fCurrentIndexPerKey = new HashMap<>();
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			pClient.logWithOutGameId(e);
		}

		if (IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON
			.equals(pClient.getProperty(CommonProperty.SETTING_LOCAL_ICON_CACHE))) {
			localCacheFolder = pClient.getProperty(CommonProperty.SETTING_LOCAL_ICON_CACHE_PATH);
			if (!localCacheFolder.endsWith(File.separator)) {
				localCacheFolder += File.separator;
			}
		}

		httpClient = setupHttpClient();
	}

	private HttpClient setupHttpClient() {
		HttpClient httpClient = null;
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> {
				final X509Certificate cert = chain[0];
				return "CN=fumbbl.com".equalsIgnoreCase(cert.getSubjectDN().getName());
			}).build();

			SSLConnectionSocketFactory sslCF = SSLConnectionSocketFactoryBuilder.create()
				.setSslContext(sslContext).build();

			int connTimeout = Integer.parseInt(fClient.getProperty(CommonProperty.HTTPCLIENT_TIMEOUT_CONNECT));
			int socketTimeout = Integer.parseInt(fClient.getProperty(CommonProperty.HTTPCLIENT_TIMEOUT_SOCKET));

			ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setConnectTimeout(Timeout.ofMilliseconds(connTimeout))
				.setSocketTimeout(Timeout.ofMilliseconds(socketTimeout))
				.build();

			PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
				.setSSLSocketFactory(sslCF).setMaxConnTotal(5).setMaxConnPerRoute(5)
				.setDefaultConnectionConfig(connectionConfig).build();

			httpClient = HttpClients.custom().setConnectionManager(cm).build();
		} catch (Exception e) {
			getClient().logWithOutGameId(e);
		}
		return httpClient;
	}

	public void init() {
		// Prevent on disk caching for image files. Improves performance for many small files.
		ImageIO.setUseCache(false);
		// Don't bother checking for certificate revocation. It's slow and not really functional anyway.
		System.setProperty("com.sun.net.ssl.checkRevocation", "false");

		fIconUrlProperties = new Properties();
		try (InputStream propertyInputStream = getClass().getResourceAsStream("/icons.ini")) {
			fIconUrlProperties.load(propertyInputStream);
		} catch (IOException pIoException) {
			// empty properties
		}

		if (StringTool.isProvided(localCacheFolder)) {
			String mapFileName = localCacheFolder + LOCAL_CACHE_MAP_FILE;
			if (!new File(mapFileName).exists()) {
				updateMapFile();
			}
			try (FileReader fileReader = new FileReader(mapFileName);
					 BufferedReader reader = new BufferedReader(fileReader)) {

				JsonObject jsonObject = JsonObject.readFrom(reader);
				localCacheMap.putAll(JSON_OPTION.getFrom(getClient(), jsonObject));

				List<String> urlsToRemove = new ArrayList<>();

				localCacheMap.forEach((url, filename) -> {
					try {
						BufferedImage image = ImageIO.read(new File(localCacheFolder + filename));
						fIconByKey.put(url, image);
					} catch (IOException e) {
						urlsToRemove.add(url);
					}
				});

				if (!urlsToRemove.isEmpty()) {
					urlsToRemove.forEach(localCacheMap::remove);
					updateMapFile();
				}

			} catch (Exception e) {
				getClient().logWithOutGameId(e);
			}
		}

	}

	public boolean loadIconFromArchive(String pUrl) {

		if (!StringTool.isProvided(pUrl)) {
			return false;
		}

		String myUrl = pUrl;
		Weather pitchWeather = findPitchWeather(myUrl);
		if (pitchWeather != null) {
			myUrl = myUrl.substring(0, myUrl.length() - 7 - pitchWeather.getShortName().length());
		}

		String iconPath = fIconUrlProperties.getProperty(myUrl);
		boolean cached = StringTool.isProvided(iconPath);

		if (!cached) {
			iconPath = myUrl;
		}
		if (!iconPath.startsWith("/")) {
			iconPath = "/" + iconPath;
		}
		if (cached && !iconPath.startsWith("/icons/cached")) {
			iconPath = "/icons/cached" + iconPath;
		}
		if (!cached && !iconPath.startsWith("/icons")) {
			iconPath = "/icons" + iconPath;
		}

		/*
		 * if (myUrl.startsWith("http:")) { if (cached) { System.out.println("cached " +
		 * myUrl + " = " + iconPath); } else { System.out.println("not cached " +
		 * myUrl); } }
		 */

		InputStream iconInputStream = null;
		ZipInputStream zipStream = null;
		try {
			iconInputStream = getClass().getResourceAsStream(iconPath);
			if (iconInputStream != null) {
				if (pitchWeather != null) {
					zipStream = new ZipInputStream(iconInputStream);
					return loadPitchFromStream(zipStream, myUrl);
				} else {
					BufferedImage icon = ImageIO.read(iconInputStream);
					iconInputStream.close();
					if (icon != null) {
						fIconByKey.put(pUrl, icon);
						return true;
					}
				}
			}
		} catch (IOException ioe) {
			// just skip precaching
		} finally {
			if (zipStream != null) {
				try {
					zipStream.close();
				} catch (IOException e) {
					// NOOP
				}
			}

			if (iconInputStream != null) {
				try {
					iconInputStream.close();
				} catch (IOException e) {
					// NOOP
				}
			}
		}

		return false;

	}

	public BufferedImage getIconByProperty(String pIconProperty) {
		if (!StringTool.isProvided(pIconProperty)) {
			return null;
		}
		String iconUrl = getClient().getProperty(pIconProperty);
		BufferedImage icon = getIconByUrl(iconUrl);
		if ((icon == null) && loadIconFromArchive(iconUrl)) {
			icon = getIconByUrl(iconUrl);
		}
		return icon;
	}

	public BufferedImage getIconByUrl(String pUrl) {
		BufferedImage bufferedImage = scaledIcons.get(pUrl);
		if (bufferedImage == null) {
			bufferedImage = fIconByKey.get(pUrl);
			if (bufferedImage != null) {
				bufferedImage = dimensionProvider.scaleImage(bufferedImage);
				scaledIcons.put(pUrl, bufferedImage);
			}
		}
		return bufferedImage;
	}

	public BufferedImage getUnscaledIconByUrl(String url) {
		return fIconByKey.get(url);
	}

	public BufferedImage getUnscaledIconByProperty(String pIconProperty) {
		if (!StringTool.isProvided(pIconProperty)) {
			return null;
		}
		String iconUrl = getClient().getProperty(pIconProperty);
		BufferedImage icon = getUnscaledIconByUrl(iconUrl);
		if ((icon == null) && loadIconFromArchive(iconUrl)) {
			icon = getUnscaledIconByUrl(iconUrl);
		}
		return icon;
	}


	public BufferedImage getPitch(Game pGame, Weather pWeather) {
		BufferedImage weatherPitch = getIconByUrl(findPitchUrl(pGame, pWeather));
		if (pWeather == Weather.INTRO || weatherPitch == null) {
			return getIconByProperty(IIconProperty.PITCH_INTRO);
		} else {
			return weatherPitch;
		}
	}

	public void loadIconFromUrl(String pUrl) {

		if (!StringTool.isProvided(pUrl)) {
			return;
		}

		Weather weather = findPitchWeather(pUrl);
		if (weather != null) {
			loadPitchFromUrl(pUrl.substring(0, pUrl.length() - 7 - weather.getShortName().length()));

		} else {
			URL iconUrl = null;
			try {
				iconUrl = new URL(pUrl);
				HttpGet get = new HttpGet(pUrl);
				httpClient.execute(get, response -> {
					final HttpEntity entity = response.getEntity();
					if (entity != null) {
						Header header = response.getHeader(HttpHeaders.CONTENT_TYPE);
						String contentType = header != null ? header.getValue() : "";
						BufferedImage icon = ImageIO.read(entity.getContent());
						EntityUtils.consumeQuietly(entity);
						fIconByKey.put(pUrl, icon);
						addLocalCacheEntry(pUrl, icon, contentType);
					}
					return null;
				});

			} catch (Exception pAny) {
				getClient().logError(0, pAny.getMessage());
				// This should catch issues where the image is broken...
				getClient().getUserInterface().getStatusReport().reportIconLoadFailure(iconUrl);
			}
		}

	}

	private void addLocalCacheEntry(String iconUrl, BufferedImage icon, String contentType) {
		if (digest == null ||
			!IClientPropertyValue.SETTING_LOCAL_ICON_CACHE_ON
				.equals(getClient().getProperty(CommonProperty.SETTING_LOCAL_ICON_CACHE))) {
			return;
		}

		digest.reset();
		digest.update(iconUrl.getBytes());
		try {
			String format = getFormat(contentType);
			String hash = DatatypeConverter.printHexBinary(digest.digest()) + "." + format;
			File newFile = new File(localCacheFolder + hash);
			if (newFile.canWrite() || newFile.createNewFile()) {
				ImageIO.write(icon, format, newFile);
				localCacheMap.put(iconUrl, hash);
				updateMapFile();
			}
		} catch (IOException e) {
			getClient().logWithOutGameId(e);
		}

	}

	private String getFormat(String contentType) throws IOException {
		Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(contentType);
		return readers.next().getFormatName();
	}

	private void updateMapFile() {
		JsonObject jsonObject = new JsonObject();
		JSON_OPTION.addTo(jsonObject, localCacheMap);
		String json = jsonObject.toString();
		try (FileWriter fileWriter = new FileWriter(localCacheFolder + LOCAL_CACHE_MAP_FILE);
				 BufferedWriter writer = new BufferedWriter(fileWriter)) {
			writer.write(json);
			writer.flush();
		} catch (IOException e) {
			getClient().logWithOutGameId(e);
		}
	}

	private Weather findPitchWeather(String pUrl) {
		Matcher pitchMatcher = _PATTERN_PITCH.matcher(pUrl);
		if (pitchMatcher.find()) {
			return new WeatherFactory().forShortName(pitchMatcher.group(1));
		}
		return null;
	}

	public String getNextProperty(String pIconProperty) {

		String nextKey;

		int index = 1;
		Integer currentIndex = fCurrentIndexPerKey.get(pIconProperty);
		if (currentIndex != null) {
			index = currentIndex + 1;
		}
		fCurrentIndexPerKey.put(pIconProperty, index);

		StringBuilder indexedProperty = new StringBuilder();
		indexedProperty.append(pIconProperty);
		indexedProperty.append(".");
		if (index < 10) {
			indexedProperty.append("0");
		}
		indexedProperty.append(index);
		nextKey = indexedProperty.toString();

		if (!StringTool.isProvided(getClient().getProperty(nextKey)) && (index > 1)) {
			fCurrentIndexPerKey.remove(pIconProperty);
			nextKey = getNextProperty(pIconProperty);
		}

		return nextKey;

	}

	public BufferedImage getPushbackIcon(Direction direction, boolean selected) {
		if (selected) {
			switch (direction) {
				case NORTH:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTH_SELECTED);
				case NORTHEAST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHEAST_SELECTED);
				case EAST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_EAST_SELECTED);
				case SOUTHEAST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHEAST_SELECTED);
				case SOUTH:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTH_SELECTED);
				case SOUTHWEST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHWEST_SELECTED);
				case WEST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_WEST_SELECTED);
				case NORTHWEST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHWEST_SELECTED);
				default:
					return null;
			}
		} else {
			switch (direction) {
				case NORTH:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTH);
				case NORTHEAST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHEAST);
				case EAST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_EAST);
				case SOUTHEAST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHEAST);
				case SOUTH:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTH);
				case SOUTHWEST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_SOUTHWEST);
				case WEST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_WEST);
				case NORTHWEST:
					return getIconByProperty(IIconProperty.GAME_PUSHBACK_NORTHWEST);
				default:
					return null;
			}
		}
	}

	public BufferedImage getIcon(BloodSpot pBloodspot) {
		String iconProperty = pBloodspot.getIconProperty();
		if (iconProperty == null) {
			switch (pBloodspot.getInjury().getBase()) {
				case PlayerState.KNOCKED_OUT:
					iconProperty = getNextProperty(IIconProperty.BLOODSPOT_KO);
					break;
				case PlayerState.BADLY_HURT:
					iconProperty = getNextProperty(IIconProperty.BLOODSPOT_BH);
					break;
				case PlayerState.SERIOUS_INJURY:
					iconProperty = getNextProperty(IIconProperty.BLOODSPOT_SI);
					break;
				case PlayerState.RIP:
					iconProperty = getNextProperty(IIconProperty.BLOODSPOT_RIP);
					break;
				case PlayerState.HIT_BY_BOMB:
					iconProperty = getNextProperty(IIconProperty.BLOODSPOT_BOMB);
					break;
				case PlayerState.HIT_BY_FIREBALL:
					iconProperty = IIconProperty.BLOODSPOT_FIREBALL;
					break;
				case PlayerState.HIT_BY_LIGHTNING:
					iconProperty = IIconProperty.BLOODSPOT_LIGHTNING;
					break;
				default:
					throw new IllegalArgumentException("Cannot get icon for blood spot with injury " + pBloodspot.getInjury() + ".");
			}
			pBloodspot.setIconProperty(iconProperty);
		}
		return getIconByProperty(iconProperty);
	}

	private String findPitchUrl(Game pGame, Weather pWeather) {
		if ((pGame == null) || (pWeather == null)) {
			return null;
		}
		Weather myWeather = pWeather;
		if (IClientPropertyValue.SETTING_PITCH_WEATHER_OFF
			.equals(getClient().getProperty(CommonProperty.SETTING_PITCH_WEATHER))) {
			myWeather = Weather.NICE;
		}
		String pitchUrl = pGame.getOptions().getOptionWithDefault(GameOptionId.PITCH_URL).getValueAsString();
		if (!StringTool.isProvided(pitchUrl) || IClientPropertyValue.SETTING_PITCH_DEFAULT
			.equals(getClient().getProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION))) {
			pitchUrl = getClient().getProperty(IIconProperty.PITCH_URL_DEFAULT);
		}
		if (IClientPropertyValue.SETTING_PITCH_BASIC
			.equals(getClient().getProperty(CommonProperty.SETTING_PITCH_CUSTOMIZATION))) {
			pitchUrl = getClient().getProperty(IIconProperty.PITCH_URL_BASIC);
		}
		return buildPitchUrl(pitchUrl, myWeather);
	}

	public String buildPitchUrl(String pUrl, Weather pWeather) {
		if (!StringTool.isProvided(pUrl) || (pWeather == null)) {
			return null;
		}
		return pUrl + "?pitch=" + pWeather.getShortName();
	}

	private void loadPitchFromUrl(String pUrl) {
		URL pitchUrl = null;
		try {
			pitchUrl = new URL(pUrl);

			HttpGet get = new HttpGet(pUrl);
			httpClient.execute(get, response -> {
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					try (ZipInputStream zipStream = new ZipInputStream(entity.getContent())) {
						loadPitchFromStream(zipStream, pUrl);
						EntityUtils.consumeQuietly(entity);
					}
				}
				return null;
			});

		} catch (Exception pAny) {
			getClient().logError(0, pAny.getMessage());
			// This should catch issues where the image is broken...
			getClient().getUserInterface().getStatusReport().reportIconLoadFailure(pitchUrl);
		}
	}

	private boolean loadPitchFromStream(ZipInputStream pZipIn, String pUrl) {
		URL pitchUrl = null;
		boolean pitchLoaded = false;
		try {
			pitchUrl = new URL(pUrl);
			Properties pitchProperties = new Properties();
			Map<String, BufferedImage> iconByName = new HashMap<>();
			ZipEntry entry;
			while ((entry = pZipIn.getNextEntry()) != null) {
				if ("pitch.ini".equals(entry.getName())) {
					pitchProperties.load(pZipIn);
				} else {
					iconByName.put(entry.getName(), ImageIO.read(pZipIn));
				}
			}
			for (Weather weather : Weather.values()) {
				String iconName = pitchProperties.getProperty(weather.getShortName());
				if (!StringTool.isProvided(iconName)) {
					continue;
				}
				BufferedImage pitchIcon = iconByName.get(iconName);
				if (pitchIcon == null) {
					continue;
				}
				fIconByKey.put(buildPitchUrl(pUrl, weather), pitchIcon);
				pitchLoaded = true;
			}
		} catch (Exception pAny) {
			getClient().logWithOutGameId(pAny);
			// This should catch issues where the image is broken...
			getClient().getUserInterface().getStatusReport().reportIconLoadFailure(pitchUrl);
		}
		return pitchLoaded;
	}

	public BufferedImage getIcon(DiceDecoration pDiceDecoration) {
		String iconProperty = null;
		switch (pDiceDecoration.getNrOfDice()) {
			case -3:
				iconProperty = IIconProperty.DECORATION_DICE_3_AGAINST;
				break;
			case -2:
				iconProperty = IIconProperty.DECORATION_DICE_2_AGAINST;
				break;
			case 1:
				iconProperty = IIconProperty.DECORATION_DICE_1;
				break;
			case 2:
				iconProperty = IIconProperty.DECORATION_DICE_2;
				break;
			case 3:
				iconProperty = IIconProperty.DECORATION_DICE_3;
				break;
			case 0:
				if (pDiceDecoration.getBlockKind() == BlockKind.VOMIT) {
					iconProperty = IIconProperty.DECORATION_VOMIT_TARGET;
				} else if (pDiceDecoration.getBlockKind() == BlockKind.CHAINSAW) {
					iconProperty = IIconProperty.DECORATION_CHAINSAW_TARGET;
				}
				break;
			default:
				break;
		}
		if (iconProperty != null) {
			return getIconByProperty(iconProperty);
		} else {
			return null;
		}
	}

	public BufferedImage getDiceIcon(int pRoll) {
		switch (pRoll) {
			case 1:
				return getIconByProperty(IIconProperty.DICE_BLOCK_1);
			case 2:
				return getIconByProperty(IIconProperty.DICE_BLOCK_2);
			case 3:
				return getIconByProperty(IIconProperty.DICE_BLOCK_3);
			case 4:
				return getIconByProperty(IIconProperty.DICE_BLOCK_4);
			case 5:
				return getIconByProperty(IIconProperty.DICE_BLOCK_5);
			case 6:
				return getIconByProperty(IIconProperty.DICE_BLOCK_6);
			default:
				break;
		}
		return null;
	}

	public static String findTeamLogoUrl(Team pTeam) {
		String iconUrl = null;
		if ((pTeam != null) && StringTool.isProvided(pTeam.getLogoUrl())) {
			if (StringTool.isProvided(pTeam.getBaseIconPath())) {
				iconUrl = UtilUrl.createUrl(pTeam.getBaseIconPath(), pTeam.getLogoUrl());
			} else {
				iconUrl = pTeam.getLogoUrl();
			}
		}
		return iconUrl;
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void clear() {
		scaledIcons.clear();
	}
}
