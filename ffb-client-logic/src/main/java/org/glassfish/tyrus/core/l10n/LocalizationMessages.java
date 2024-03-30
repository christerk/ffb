
package org.glassfish.tyrus.core.l10n;

/**
 * Defines string formatting method for each constant in the resource file
 * 
 */
public final class LocalizationMessages {

	private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory(
			"org.glassfish.tyrus.core.l10n.localization");
	private final static Localizer localizer = new Localizer();

	public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
		return messageFactory.getMessage("illegal.provider.class.name", arg0);
	}

	/**
	 * Illegal provider-class name: {0}.
	 * 
	 */
	public static String ILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
		return localizer.localize(localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
	}

	public static Localizable localizableMESSAGE_HANDLER_ALREADY_REGISTERED_PONG() {
		return messageFactory.getMessage("message.handler.already.registered.pong");
	}

	/**
	 * Pong MessageHandler already registered.
	 * 
	 */
	public static String MESSAGE_HANDLER_ALREADY_REGISTERED_PONG() {
		return localizer.localize(localizableMESSAGE_HANDLER_ALREADY_REGISTERED_PONG());
	}

	public static Localizable localizableENDPOINT_WRONG_PATH_PARAM(Object arg0, Object arg1) {
		return messageFactory.getMessage("endpoint.wrong.path.param", arg0, arg1);
	}

	/**
	 * Method: {0}: {1} is not allowed type for @PathParameter.
	 * 
	 */
	public static String ENDPOINT_WRONG_PATH_PARAM(Object arg0, Object arg1) {
		return localizer.localize(localizableENDPOINT_WRONG_PATH_PARAM(arg0, arg1));
	}

	public static Localizable localizableSESSION_CLOSED_IDLE_TIMEOUT() {
		return messageFactory.getMessage("session.closed.idle.timeout");
	}

	/**
	 * "Session closed by the container because of the idle timeout."
	 * 
	 */
	public static String SESSION_CLOSED_IDLE_TIMEOUT() {
		return localizer.localize(localizableSESSION_CLOSED_IDLE_TIMEOUT());
	}

	public static Localizable localizableENDPOINT_EXCEPTION_FROM_ON_ERROR(Object arg0) {
		return messageFactory.getMessage("endpoint.exception.from.on.error", arg0);
	}

	/**
	 * Exception thrown from onError method {0}.
	 * 
	 */
	public static String ENDPOINT_EXCEPTION_FROM_ON_ERROR(Object arg0) {
		return localizer.localize(localizableENDPOINT_EXCEPTION_FROM_ON_ERROR(arg0));
	}

	public static Localizable localizableENDPOINT_MAX_MESSAGE_SIZE_TOO_LONG(Object arg0, Object arg1, Object arg2,
			Object arg3) {
		return messageFactory.getMessage("endpoint.max.message.size.too.long", arg0, arg1, arg2, arg3);
	}

	/**
	 * MaxMessageSize {0} on method {1} in endpoint {2} is larger than the container
	 * incoming buffer size {3}.
	 * 
	 */
	public static String ENDPOINT_MAX_MESSAGE_SIZE_TOO_LONG(Object arg0, Object arg1, Object arg2, Object arg3) {
		return localizer.localize(localizableENDPOINT_MAX_MESSAGE_SIZE_TOO_LONG(arg0, arg1, arg2, arg3));
	}

	public static Localizable localizableENCODING_FAILED() {
		return messageFactory.getMessage("encoding.failed");
	}

	/**
	 * Encoding failed.
	 * 
	 */
	public static String ENCODING_FAILED() {
		return localizer.localize(localizableENCODING_FAILED());
	}

	public static Localizable localizableCLASS_NOT_INSTANTIATED(Object arg0) {
		return messageFactory.getMessage("class.not.instantiated", arg0);
	}

	/**
	 * Class {0} couldn''t be instantiated.
	 * 
	 */
	public static String CLASS_NOT_INSTANTIATED(Object arg0) {
		return localizer.localize(localizableCLASS_NOT_INSTANTIATED(arg0));
	}

	public static Localizable localizableCONNECTION_NULL() {
		return messageFactory.getMessage("connection.null");
	}

	/**
	 * Connection is null.
	 * 
	 */
	public static String CONNECTION_NULL() {
		return localizer.localize(localizableCONNECTION_NULL());
	}

	public static Localizable localizableHANDSHAKE_HTTP_REDIRECTION_MAX_REDIRECTION(Object arg0) {
		return messageFactory.getMessage("handshake.http.redirection.max.redirection", arg0);
	}

	/**
	 * Redirect attempts exceeded maximum allowed limit of ({0}) redirects.
	 * 
	 */
	public static String HANDSHAKE_HTTP_REDIRECTION_MAX_REDIRECTION(Object arg0) {
		return localizer.localize(localizableHANDSHAKE_HTTP_REDIRECTION_MAX_REDIRECTION(arg0));
	}

	public static Localizable localizableNO_DECODER_FOUND() {
		return messageFactory.getMessage("no.decoder.found");
	}

	/**
	 * No decoder found.
	 * 
	 */
	public static String NO_DECODER_FOUND() {
		return localizer.localize(localizableNO_DECODER_FOUND());
	}

	public static Localizable localizablePARTIAL_MESSAGE_BUFFER_OVERFLOW() {
		return messageFactory.getMessage("partial.message.buffer.overflow");
	}

	/**
	 * Partial message could not be delivered due to buffer overflow.
	 * 
	 */
	public static String PARTIAL_MESSAGE_BUFFER_OVERFLOW() {
		return localizer.localize(localizablePARTIAL_MESSAGE_BUFFER_OVERFLOW());
	}

	public static Localizable localizableUNEXPECTED_ERROR_CONNECTION_CLOSE() {
		return messageFactory.getMessage("unexpected.error.connection.close");
	}

	/**
	 * Unexpected error, closing connection.
	 * 
	 */
	public static String UNEXPECTED_ERROR_CONNECTION_CLOSE() {
		return localizer.localize(localizableUNEXPECTED_ERROR_CONNECTION_CLOSE());
	}

	public static Localizable localizableENDPOINT_MULTIPLE_SESSION_PARAM(Object arg0) {
		return messageFactory.getMessage("endpoint.multiple.session.param", arg0);
	}

	/**
	 * Method {0} has got two or more Session parameters.
	 * 
	 */
	public static String ENDPOINT_MULTIPLE_SESSION_PARAM(Object arg0) {
		return localizer.localize(localizableENDPOINT_MULTIPLE_SESSION_PARAM(arg0));
	}

	public static Localizable localizableURI_COMPONENT_INVALID_CHARACTER(Object arg0, Object arg1, Object arg2,
			Object arg3) {
		return messageFactory.getMessage("uri.component.invalid.character", arg0, arg1, arg2, arg3);
	}

	/**
	 * The string "{0}" for the URI component {1} contains an invalid character,
	 * ''{2}'', at index {3}.
	 * 
	 */
	public static String URI_COMPONENT_INVALID_CHARACTER(Object arg0, Object arg1, Object arg2, Object arg3) {
		return localizer.localize(localizableURI_COMPONENT_INVALID_CHARACTER(arg0, arg1, arg2, arg3));
	}

	public static Localizable localizableCOMPONENT_PROVIDER_THREW_EXCEPTION(Object arg0) {
		return messageFactory.getMessage("component.provider.threw.exception", arg0);
	}

	/**
	 * Component provider threw exception when providing instance of class {0}.
	 * 
	 */
	public static String COMPONENT_PROVIDER_THREW_EXCEPTION(Object arg0) {
		return localizer.localize(localizableCOMPONENT_PROVIDER_THREW_EXCEPTION(arg0));
	}

	public static Localizable localizableHANDSHAKE_HTTP_REDIRECTION_NOT_ENABLED(Object arg0) {
		return messageFactory.getMessage("handshake.http.redirection.not.enabled", arg0);
	}

	/**
	 * Received HTTP response status code: {0} and redirect processing is not
	 * enabled.
	 * 
	 */
	public static String HANDSHAKE_HTTP_REDIRECTION_NOT_ENABLED(Object arg0) {
		return localizer.localize(localizableHANDSHAKE_HTTP_REDIRECTION_NOT_ENABLED(arg0));
	}

	public static Localizable localizableFRAGMENT_INVALID_OPCODE() {
		return messageFactory.getMessage("fragment.invalid.opcode");
	}

	/**
	 * Fragment sent but opcode was not 0.
	 * 
	 */
	public static String FRAGMENT_INVALID_OPCODE() {
		return localizer.localize(localizableFRAGMENT_INVALID_OPCODE());
	}

	public static Localizable localizableINVALID_RESPONSE_CODE(Object arg0, Object arg1) {
		return messageFactory.getMessage("invalid.response.code", arg0, arg1);
	}

	/**
	 * Response code was not {0}: {1}.
	 * 
	 */
	public static String INVALID_RESPONSE_CODE(Object arg0, Object arg1) {
		return localizer.localize(localizableINVALID_RESPONSE_CODE(arg0, arg1));
	}

	public static Localizable localizableIOEXCEPTION_CLOSE() {
		return messageFactory.getMessage("ioexception.close");
	}

	/**
	 * IOException thrown when closing connection.
	 * 
	 */
	public static String IOEXCEPTION_CLOSE() {
		return localizer.localize(localizableIOEXCEPTION_CLOSE());
	}

	public static Localizable localizableCLASS_CHECKER_FORBIDDEN_RETURN_TYPE(Object arg0, Object arg1) {
		return messageFactory.getMessage("class.checker.forbidden.return.type", arg0, arg1);
	}

	/**
	 * Method: {0}.{1}: has got unsupported return type.
	 * 
	 */
	public static String CLASS_CHECKER_FORBIDDEN_RETURN_TYPE(Object arg0, Object arg1) {
		return localizer.localize(localizableCLASS_CHECKER_FORBIDDEN_RETURN_TYPE(arg0, arg1));
	}

	public static Localizable localizableMAX_SESSIONS_PER_REMOTEADDR_EXCEEDED() {
		return messageFactory.getMessage("max.sessions.per.remoteaddr.exceeded");
	}

	/**
	 * Maximal number of open sessions per remote address exceeded.
	 * 
	 */
	public static String MAX_SESSIONS_PER_REMOTEADDR_EXCEEDED() {
		return localizer.localize(localizableMAX_SESSIONS_PER_REMOTEADDR_EXCEEDED());
	}

	public static Localizable localizableBINARY_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return messageFactory.getMessage("binary.message.out.of.order", arg0);
	}

	/**
	 * Binary message received out of order. Session: ''{0}''.
	 * 
	 */
	public static String BINARY_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return localizer.localize(localizableBINARY_MESSAGE_OUT_OF_ORDER(arg0));
	}

	public static Localizable localizableEXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
		return messageFactory.getMessage("exception.caught.while.loading.spi.providers");
	}

	/**
	 * Exception caught while loading SPI providers.
	 * 
	 */
	public static String EXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
		return localizer.localize(localizableEXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS());
	}

	public static Localizable localizableBUFFER_OVERFLOW() {
		return messageFactory.getMessage("buffer.overflow");
	}

	/**
	 * Buffer overflow.
	 * 
	 */
	public static String BUFFER_OVERFLOW() {
		return localizer.localize(localizableBUFFER_OVERFLOW());
	}

	public static Localizable localizableURI_COMPONENT_ENCODED_OCTET_MALFORMED(Object arg0) {
		return messageFactory.getMessage("uri.component.encoded.octet.malformed", arg0);
	}

	/**
	 * Malformed percent-encoded octet at index {0}.
	 * 
	 */
	public static String URI_COMPONENT_ENCODED_OCTET_MALFORMED(Object arg0) {
		return localizer.localize(localizableURI_COMPONENT_ENCODED_OCTET_MALFORMED(arg0));
	}

	public static Localizable localizableUNEXPECTED_END_FRAGMENT() {
		return messageFactory.getMessage("unexpected.end.fragment");
	}

	/**
	 * End fragment sent, but wasn''t processing any previous fragments.
	 * 
	 */
	public static String UNEXPECTED_END_FRAGMENT() {
		return localizer.localize(localizableUNEXPECTED_END_FRAGMENT());
	}

	public static Localizable localizableDATA_UNEXPECTED_LENGTH(Object arg0, Object arg1) {
		return messageFactory.getMessage("data.unexpected.length", arg0, arg1);
	}

	/**
	 * Data read ({0}) is not the expected size ({1}).
	 * 
	 */
	public static String DATA_UNEXPECTED_LENGTH(Object arg0, Object arg1) {
		return localizer.localize(localizableDATA_UNEXPECTED_LENGTH(arg0, arg1));
	}

	public static Localizable localizableCLASS_CHECKER_FORBIDDEN_WEB_SOCKET_OPEN_PARAM(Object arg0, Object arg1,
			Object arg2) {
		return messageFactory.getMessage("class.checker.forbidden.web.socket.open.param", arg0, arg1, arg2);
	}

	/**
	 * Method: {0}.{1}: {2} has got multiple parameters of identical type.
	 * 
	 */
	public static String CLASS_CHECKER_FORBIDDEN_WEB_SOCKET_OPEN_PARAM(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizableCLASS_CHECKER_FORBIDDEN_WEB_SOCKET_OPEN_PARAM(arg0, arg1, arg2));
	}

	public static Localizable localizableCLASS_CHECKER_FORBIDDEN_WEB_SOCKET_CLOSE_PARAM(Object arg0, Object arg1) {
		return messageFactory.getMessage("class.checker.forbidden.web.socket.close.param", arg0, arg1);
	}

	/**
	 * Method: {0}.{1}: @OnClose has got different params than Session or
	 * CloseReason.
	 * 
	 */
	public static String CLASS_CHECKER_FORBIDDEN_WEB_SOCKET_CLOSE_PARAM(Object arg0, Object arg1) {
		return localizer.localize(localizableCLASS_CHECKER_FORBIDDEN_WEB_SOCKET_CLOSE_PARAM(arg0, arg1));
	}

	public static Localizable localizableBINARY_MESSAGE_HANDLER_NOT_FOUND(Object arg0) {
		return messageFactory.getMessage("binary.message.handler.not.found", arg0);
	}

	/**
	 * Binary message handler not found. Session: ''{0}''.
	 * 
	 */
	public static String BINARY_MESSAGE_HANDLER_NOT_FOUND(Object arg0) {
		return localizer.localize(localizableBINARY_MESSAGE_HANDLER_NOT_FOUND(arg0));
	}

	public static Localizable localizableHEADERS_MISSING() {
		return messageFactory.getMessage("headers.missing");
	}

	/**
	 * Missing headers required for WebSocket negotiation.
	 * 
	 */
	public static String HEADERS_MISSING() {
		return localizer.localize(localizableHEADERS_MISSING());
	}

	public static Localizable localizableUNEXPECTED_STATE(Object arg0) {
		return messageFactory.getMessage("unexpected.state", arg0);
	}

	/**
	 * Unexpected state: {0}.
	 * 
	 */
	public static String UNEXPECTED_STATE(Object arg0) {
		return localizer.localize(localizableUNEXPECTED_STATE(arg0));
	}

	public static Localizable localizableTEXT_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return messageFactory.getMessage("text.message.out.of.order", arg0);
	}

	/**
	 * Text message received out of order. Session: ''{0}''.
	 * 
	 */
	public static String TEXT_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return localizer.localize(localizableTEXT_MESSAGE_OUT_OF_ORDER(arg0));
	}

	public static Localizable localizableTEXT_MESSAGE_HANDLER_NOT_FOUND(Object arg0) {
		return messageFactory.getMessage("text.message.handler.not.found", arg0);
	}

	/**
	 * Text message handler not found. Session: ''{0}''.
	 * 
	 */
	public static String TEXT_MESSAGE_HANDLER_NOT_FOUND(Object arg0) {
		return localizer.localize(localizableTEXT_MESSAGE_HANDLER_NOT_FOUND(arg0));
	}

	public static Localizable localizableEXTENSION_EXCEPTION(Object arg0, Object arg1) {
		return messageFactory.getMessage("extension.exception", arg0, arg1);
	}

	/**
	 * Extension ''{0}'' threw an exception during processOutgoing method
	 * invocation: "{1}".
	 * 
	 */
	public static String EXTENSION_EXCEPTION(Object arg0, Object arg1) {
		return localizer.localize(localizableEXTENSION_EXCEPTION(arg0, arg1));
	}

	public static Localizable localizableMESSAGE_HANDLER_ALREADY_REGISTERED_BINARY() {
		return messageFactory.getMessage("message.handler.already.registered.binary");
	}

	/**
	 * Binary MessageHandler already registered.
	 * 
	 */
	public static String MESSAGE_HANDLER_ALREADY_REGISTERED_BINARY() {
		return localizer.localize(localizableMESSAGE_HANDLER_ALREADY_REGISTERED_BINARY());
	}

	public static Localizable localizableARGUMENT_NOT_NULL(Object arg0) {
		return messageFactory.getMessage("argument.not.null", arg0);
	}

	/**
	 * Argument ''{0}'' cannot be null.
	 * 
	 */
	public static String ARGUMENT_NOT_NULL(Object arg0) {
		return localizer.localize(localizableARGUMENT_NOT_NULL(arg0));
	}

	public static Localizable localizableCLASS_CHECKER_ADD_MESSAGE_HANDLER_ERROR(Object arg0, Object arg1) {
		return messageFactory.getMessage("class.checker.add.message.handler.error", arg0, arg1);
	}

	/**
	 * Class {0}. {1}.
	 * 
	 */
	public static String CLASS_CHECKER_ADD_MESSAGE_HANDLER_ERROR(Object arg0, Object arg1) {
		return localizer.localize(localizableCLASS_CHECKER_ADD_MESSAGE_HANDLER_ERROR(arg0, arg1));
	}

	public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
		return messageFactory.getMessage("illegal.config.syntax");
	}

	/**
	 * Illegal configuration-file syntax.
	 * 
	 */
	public static String ILLEGAL_CONFIG_SYNTAX() {
		return localizer.localize(localizableILLEGAL_CONFIG_SYNTAX());
	}

	public static Localizable localizableRSV_INCORRECTLY_SET() {
		return messageFactory.getMessage("rsv.incorrectly.set");
	}

	/**
	 * RSV bit(s) incorrectly set.
	 * 
	 */
	public static String RSV_INCORRECTLY_SET() {
		return localizer.localize(localizableRSV_INCORRECTLY_SET());
	}

	public static Localizable localizableMESSAGE_TOO_LONG(Object arg0, Object arg1) {
		return messageFactory.getMessage("message.too.long", arg0, arg1);
	}

	/**
	 * Message too long; allowed message size is {0} bytes. (Current message length
	 * is {1} bytes).
	 * 
	 */
	public static String MESSAGE_TOO_LONG(Object arg0, Object arg1) {
		return localizer.localize(localizableMESSAGE_TOO_LONG(arg0, arg1));
	}

	public static Localizable localizableCLASS_CHECKER_FORBIDDEN_WEB_SOCKET_ERROR_PARAM(Object arg0, Object arg1,
			Object arg2) {
		return messageFactory.getMessage("class.checker.forbidden.web.socket.error.param", arg0, arg1, arg2);
	}

	/**
	 * Method: {0}.{1}: {2} is not allowed as parameter type for method annotated
	 * with @OnError.
	 * 
	 */
	public static String CLASS_CHECKER_FORBIDDEN_WEB_SOCKET_ERROR_PARAM(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizableCLASS_CHECKER_FORBIDDEN_WEB_SOCKET_ERROR_PARAM(arg0, arg1, arg2));
	}

	public static Localizable localizableSEC_KEY_INVALID_SERVER() {
		return messageFactory.getMessage("sec.key.invalid.server");
	}

	/**
	 * Sec-WebSocket-Key returned from server does not match expected response.
	 * 
	 */
	public static String SEC_KEY_INVALID_SERVER() {
		return localizer.localize(localizableSEC_KEY_INVALID_SERVER());
	}

	public static Localizable localizableSEC_KEY_INVALID_LENGTH(Object arg0) {
		return messageFactory.getMessage("sec.key.invalid.length", arg0);
	}

	/**
	 * Invalid Sec-WebSocket-Key length. Should be 20 and is: {0}.
	 * 
	 */
	public static String SEC_KEY_INVALID_LENGTH(Object arg0) {
		return localizer.localize(localizableSEC_KEY_INVALID_LENGTH(arg0));
	}

	public static Localizable localizableAUTHENTICATION_CREDENTIALS_MISSING() {
		return messageFactory.getMessage("authentication.credentials.missing");
	}

	/**
	 * Credentials are missing.
	 * 
	 */
	public static String AUTHENTICATION_CREDENTIALS_MISSING() {
		return localizer.localize(localizableAUTHENTICATION_CREDENTIALS_MISSING());
	}

	public static Localizable localizableCONTROL_FRAME_LENGTH() {
		return messageFactory.getMessage("control.frame.length");
	}

	/**
	 * Control frame payloads must be no greater than 125 bytes.
	 * 
	 */
	public static String CONTROL_FRAME_LENGTH() {
		return localizer.localize(localizableCONTROL_FRAME_LENGTH());
	}

	public static Localizable localizableUNHANDLED_TEXT_MESSAGE(Object arg0) {
		return messageFactory.getMessage("unhandled.text.message", arg0);
	}

	/**
	 * Unhandled text message. Session: {0}.
	 * 
	 */
	public static String UNHANDLED_TEXT_MESSAGE(Object arg0) {
		return localizer.localize(localizableUNHANDLED_TEXT_MESSAGE(arg0));
	}

	public static Localizable localizableHANDSHAKE_HTTP_REDIRECTION_NEW_LOCATION_ERROR(Object arg0) {
		return messageFactory.getMessage("handshake.http.redirection.new.location.error", arg0);
	}

	/**
	 * Invalid URI in Location header: "{0}".
	 * 
	 */
	public static String HANDSHAKE_HTTP_REDIRECTION_NEW_LOCATION_ERROR(Object arg0) {
		return localizer.localize(localizableHANDSHAKE_HTTP_REDIRECTION_NEW_LOCATION_ERROR(arg0));
	}

	public static Localizable localizableMESSAGE_HANDLER_ALREADY_REGISTERED_TYPE(Object arg0) {
		return messageFactory.getMessage("message.handler.already.registered.type", arg0);
	}

	/**
	 * MessageHandler for type: {0} already registered.
	 * 
	 */
	public static String MESSAGE_HANDLER_ALREADY_REGISTERED_TYPE(Object arg0) {
		return localizer.localize(localizableMESSAGE_HANDLER_ALREADY_REGISTERED_TYPE(arg0));
	}

	public static Localizable localizableENDPOINT_UNHANDLED_EXCEPTION(Object arg0) {
		return messageFactory.getMessage("endpoint.unhandled.exception", arg0);
	}

	/**
	 * Unhandled exception in endpoint {0}.
	 * 
	 */
	public static String ENDPOINT_UNHANDLED_EXCEPTION(Object arg0) {
		return localizer.localize(localizableENDPOINT_UNHANDLED_EXCEPTION(arg0));
	}

	public static Localizable localizableSEC_KEY_NULL_NOT_ALLOWED() {
		return messageFactory.getMessage("sec.key.null.not.allowed");
	}

	/**
	 * Null Sec-WebSocket-Key is not allowed.
	 * 
	 */
	public static String SEC_KEY_NULL_NOT_ALLOWED() {
		return localizer.localize(localizableSEC_KEY_NULL_NOT_ALLOWED());
	}

	public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
		return messageFactory.getMessage("dependent.class.of.provider.not.found", arg0, arg1, arg2);
	}

	/**
	 * A dependent class, {0}, of the class {1} implementing the provider {2} is not
	 * found. The provider implementation is ignored.
	 * 
	 */
	public static String DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(arg0, arg1, arg2));
	}

	public static Localizable localizableERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
		return messageFactory.getMessage("error.caught.while.loading.spi.providers");
	}

	/**
	 * Error caught while loading SPI providers.
	 * 
	 */
	public static String ERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
		return localizer.localize(localizableERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS());
	}

	public static Localizable localizableCONTROL_FRAME_FRAGMENTED() {
		return messageFactory.getMessage("control.frame.fragmented");
	}

	/**
	 * Fragmented control frame.
	 * 
	 */
	public static String CONTROL_FRAME_FRAGMENTED() {
		return localizer.localize(localizableCONTROL_FRAME_FRAGMENTED());
	}

	public static Localizable localizableAUTHENTICATION_FAILED() {
		return messageFactory.getMessage("authentication.failed");
	}

	/**
	 * Authentication failed.
	 * 
	 */
	public static String AUTHENTICATION_FAILED() {
		return localizer.localize(localizableAUTHENTICATION_FAILED());
	}

	public static Localizable localizableENDPOINT_MULTIPLE_METHODS(Object arg0, Object arg1, Object arg2, Object arg3) {
		return messageFactory.getMessage("endpoint.multiple.methods", arg0, arg1, arg2, arg3);
	}

	/**
	 * Multiple methods using {0} annotation in class {1}: {2} and {3}. The latter
	 * will be ignored.
	 * 
	 */
	public static String ENDPOINT_MULTIPLE_METHODS(Object arg0, Object arg1, Object arg2, Object arg3) {
		return localizer.localize(localizableENDPOINT_MULTIPLE_METHODS(arg0, arg1, arg2, arg3));
	}

	public static Localizable localizableCLASS_CHECKER_MULTIPLE_IDENTICAL_PARAMS(Object arg0, Object arg1) {
		return messageFactory.getMessage("class.checker.multiple.identical.params", arg0, arg1);
	}

	/**
	 * Method: {0}.{1}: has got multiple parameters of identical type.
	 * 
	 */
	public static String CLASS_CHECKER_MULTIPLE_IDENTICAL_PARAMS(Object arg0, Object arg1) {
		return localizer.localize(localizableCLASS_CHECKER_MULTIPLE_IDENTICAL_PARAMS(arg0, arg1));
	}

	public static Localizable localizableMESSAGE_HANDLER_DECODER_NOT_REGISTERED(Object arg0) {
		return messageFactory.getMessage("message.handler.decoder.not.registered", arg0);
	}

	/**
	 * Decoder for type: {0} has not been registered.
	 * 
	 */
	public static String MESSAGE_HANDLER_DECODER_NOT_REGISTERED(Object arg0) {
		return localizer.localize(localizableMESSAGE_HANDLER_DECODER_NOT_REGISTERED(arg0));
	}

	public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(Object arg0, Object arg1, Object arg2) {
		return messageFactory.getMessage("dependent.class.of.provider.format.error", arg0, arg1, arg2);
	}

	/**
	 * {0}. A dependent class of the class {1} implementing the provider {2} is
	 * malformed. The provider implementation is ignored. Check if the malformed
	 * class is part of a stubbed jar that used for compiling only.
	 * 
	 */
	public static String DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(arg0, arg1, arg2));
	}

	public static Localizable localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(Object arg0, Object arg1, Object arg2) {
		return messageFactory.getMessage("provider.class.could.not.be.loaded", arg0, arg1, arg2);
	}

	/**
	 * The class {0} implementing provider {1} could not be loaded: {2}
	 * 
	 */
	public static String PROVIDER_CLASS_COULD_NOT_BE_LOADED(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(arg0, arg1, arg2));
	}

	public static Localizable localizableMESSAGE_HANDLER_ILLEGAL_ARGUMENT(Object arg0) {
		return messageFactory.getMessage("message.handler.illegal.argument", arg0);
	}

	/**
	 * Illegal MessageHandler argument value: {0}.
	 * 
	 */
	public static String MESSAGE_HANDLER_ILLEGAL_ARGUMENT(Object arg0) {
		return localizer.localize(localizableMESSAGE_HANDLER_ILLEGAL_ARGUMENT(arg0));
	}

	public static Localizable localizableAPPLICATION_DATA_TOO_LONG(Object arg0) {
		return messageFactory.getMessage("application.data.too.long", arg0);
	}

	/**
	 * {0} applicationData exceeded the maximum allowed payload of 125 bytes.
	 * 
	 */
	public static String APPLICATION_DATA_TOO_LONG(Object arg0) {
		return localizer.localize(localizableAPPLICATION_DATA_TOO_LONG(arg0));
	}

	public static Localizable localizableAUTHENTICATION_DIGEST_NO_SUCH_ALG() {
		return messageFactory.getMessage("authentication.digest.no.such.alg");
	}

	/**
	 * No such algorithm to generate authorization digest http header.
	 * 
	 */
	public static String AUTHENTICATION_DIGEST_NO_SUCH_ALG() {
		return localizer.localize(localizableAUTHENTICATION_DIGEST_NO_SUCH_ALG());
	}

	public static Localizable localizableCONNECTION_HAS_BEEN_CLOSED() {
		return messageFactory.getMessage("connection.has.been.closed");
	}

	/**
	 * The connection has been closed.
	 * 
	 */
	public static String CONNECTION_HAS_BEEN_CLOSED() {
		return localizer.localize(localizableCONNECTION_HAS_BEEN_CLOSED());
	}

	public static Localizable localizableENDPOINT_ANNOTATION_NOT_FOUND(Object arg0, Object arg1) {
		return messageFactory.getMessage("endpoint.annotation.not.found", arg0, arg1);
	}

	/**
	 * {0} annotation not found on class {1}.
	 * 
	 */
	public static String ENDPOINT_ANNOTATION_NOT_FOUND(Object arg0, Object arg1) {
		return localizer.localize(localizableENDPOINT_ANNOTATION_NOT_FOUND(arg0, arg1));
	}

	public static Localizable localizablePARTIAL_TEXT_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return messageFactory.getMessage("partial.text.message.out.of.order", arg0);
	}

	/**
	 * Partial text message received out of order. Session: ''{0}''.
	 * 
	 */
	public static String PARTIAL_TEXT_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return localizer.localize(localizablePARTIAL_TEXT_MESSAGE_OUT_OF_ORDER(arg0));
	}

	public static Localizable localizableHANDSHAKE_HTTP_REDIRECTION_NEW_LOCATION_MISSING() {
		return messageFactory.getMessage("handshake.http.redirection.new.location.missing");
	}

	/**
	 * Missing or empty Location header in 3xx HTTP Redirect response.
	 * 
	 */
	public static String HANDSHAKE_HTTP_REDIRECTION_NEW_LOCATION_MISSING() {
		return localizer.localize(localizableHANDSHAKE_HTTP_REDIRECTION_NEW_LOCATION_MISSING());
	}

	public static Localizable localizableURI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(Object arg0, Object arg1) {
		return messageFactory.getMessage("uri.component.encoded.octet.invalid.digit", arg0, arg1);
	}

	/**
	 * Malformed percent-encoded octet at index {0}, invalid hexadecimal digit
	 * ''{1}''.
	 * 
	 */
	public static String URI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(Object arg0, Object arg1) {
		return localizer.localize(localizableURI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(arg0, arg1));
	}

	public static Localizable localizableEQUIVALENT_PATHS(Object arg0, Object arg1) {
		return messageFactory.getMessage("equivalent.paths", arg0, arg1);
	}

	/**
	 * Found equivalent paths. Added path: ''{0}'' is equivalent with ''{1}''.
	 * 
	 */
	public static String EQUIVALENT_PATHS(Object arg0, Object arg1) {
		return localizer.localize(localizableEQUIVALENT_PATHS(arg0, arg1));
	}

	public static Localizable localizableCLASS_CHECKER_MANDATORY_PARAM_MISSING(Object arg0, Object arg1) {
		return messageFactory.getMessage("class.checker.mandatory.param.missing", arg0, arg1);
	}

	/**
	 * Method: {0}.{1}: does not have mandatory Throwable param.
	 * 
	 */
	public static String CLASS_CHECKER_MANDATORY_PARAM_MISSING(Object arg0, Object arg1) {
		return localizer.localize(localizableCLASS_CHECKER_MANDATORY_PARAM_MISSING(arg0, arg1));
	}

	public static Localizable localizableMAX_SESSIONS_PER_APP_EXCEEDED() {
		return messageFactory.getMessage("max.sessions.per.app.exceeded");
	}

	/**
	 * Maximal number of open sessions per application exceeded.
	 * 
	 */
	public static String MAX_SESSIONS_PER_APP_EXCEEDED() {
		return localizer.localize(localizableMAX_SESSIONS_PER_APP_EXCEEDED());
	}

	public static Localizable localizableMESSAGE_HANDLER_ALREADY_REGISTERED_TEXT() {
		return messageFactory.getMessage("message.handler.already.registered.text");
	}

	/**
	 * Text MessageHandler already registered.
	 * 
	 */
	public static String MESSAGE_HANDLER_ALREADY_REGISTERED_TEXT() {
		return localizer.localize(localizableMESSAGE_HANDLER_ALREADY_REGISTERED_TEXT());
	}

	public static Localizable localizableSEND_MESSAGE_INFRAGMENT() {
		return messageFactory.getMessage("send.message.infragment");
	}

	/**
	 * Attempting to send a message while sending fragments of another.
	 * 
	 */
	public static String SEND_MESSAGE_INFRAGMENT() {
		return localizer.localize(localizableSEND_MESSAGE_INFRAGMENT());
	}

	public static Localizable localizableHANDSHAKE_HTTP_REDIRECTION_INFINITE_LOOP() {
		return messageFactory.getMessage("handshake.http.redirection.infinite.loop");
	}

	/**
	 * Infinite HTTP Redirection loop detected.
	 * 
	 */
	public static String HANDSHAKE_HTTP_REDIRECTION_INFINITE_LOOP() {
		return localizer.localize(localizableHANDSHAKE_HTTP_REDIRECTION_INFINITE_LOOP());
	}

	public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1, Object arg2) {
		return messageFactory.getMessage("provider.could.not.be.created", arg0, arg1, arg2);
	}

	/**
	 * The class {0} implementing provider {1} could not be instantiated: {2}
	 * 
	 */
	public static String PROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1, arg2));
	}

	public static Localizable localizableENDPOINT_UNKNOWN_PARAMS(Object arg0, Object arg1, Object arg2) {
		return messageFactory.getMessage("endpoint.unknown.params", arg0, arg1, arg2);
	}

	/**
	 * Unknown parameter(s) for {0}.{1} method annotated with @OnError annotation:
	 * {2}. This method will be ignored.
	 * 
	 */
	public static String ENDPOINT_UNKNOWN_PARAMS(Object arg0, Object arg1, Object arg2) {
		return localizer.localize(localizableENDPOINT_UNKNOWN_PARAMS(arg0, arg1, arg2));
	}

	public static Localizable localizableAUTHENTICATION_DIGEST_QOP_UNSUPPORTED(Object arg0) {
		return messageFactory.getMessage("authentication.digest.qop.unsupported", arg0);
	}

	/**
	 * The 'qop' (quality of protection) = {0} extension requested by the server is
	 * not supported by Tyrus. Cannot authenticate against the server using Http
	 * Digest Authentication.
	 * 
	 */
	public static String AUTHENTICATION_DIGEST_QOP_UNSUPPORTED(Object arg0) {
		return localizer.localize(localizableAUTHENTICATION_DIGEST_QOP_UNSUPPORTED(arg0));
	}

	public static Localizable localizableAUTHENTICATION_CREATE_AUTH_HEADER_FAILED() {
		return messageFactory.getMessage("authentication.create.auth.header.failed");
	}

	/**
	 * Could not add authorization header in http request.
	 * 
	 */
	public static String AUTHENTICATION_CREATE_AUTH_HEADER_FAILED() {
		return localizer.localize(localizableAUTHENTICATION_CREATE_AUTH_HEADER_FAILED());
	}

	public static Localizable localizableINVALID_HEADER(Object arg0, Object arg1) {
		return messageFactory.getMessage("invalid.header", arg0, arg1);
	}

	/**
	 * Invalid {0} header value: "{1}".
	 * 
	 */
	public static String INVALID_HEADER(Object arg0, Object arg1) {
		return localizer.localize(localizableINVALID_HEADER(arg0, arg1));
	}

	public static Localizable localizableENDPOINT_WRONG_PARAMS(Object arg0, Object arg1) {
		return messageFactory.getMessage("endpoint.wrong.params", arg0, arg1);
	}

	/**
	 * Method: {0}.{1}: has got wrong number of params.
	 * 
	 */
	public static String ENDPOINT_WRONG_PARAMS(Object arg0, Object arg1) {
		return localizer.localize(localizableENDPOINT_WRONG_PARAMS(arg0, arg1));
	}

	public static Localizable localizableHANDSHAKE_HTTP_RETRY_AFTER_MESSAGE() {
		return messageFactory.getMessage("handshake.http.retry.after.message");
	}

	/**
	 * HTTP Status code 503 - Service Unavailable.
	 * 
	 */
	public static String HANDSHAKE_HTTP_RETRY_AFTER_MESSAGE() {
		return localizer.localize(localizableHANDSHAKE_HTTP_RETRY_AFTER_MESSAGE());
	}

	public static Localizable localizableCOMPONENT_PROVIDER_NOT_FOUND(Object arg0) {
		return messageFactory.getMessage("component.provider.not.found", arg0);
	}

	/**
	 * Component provider for class {0} not found.
	 * 
	 */
	public static String COMPONENT_PROVIDER_NOT_FOUND(Object arg0) {
		return localizer.localize(localizableCOMPONENT_PROVIDER_NOT_FOUND(arg0));
	}

	public static Localizable localizableILLEGAL_UTF_8_SEQUENCE() {
		return messageFactory.getMessage("illegal.utf8.sequence");
	}

	/**
	 * Illegal UTF-8 Sequence.
	 * 
	 */
	public static String ILLEGAL_UTF_8_SEQUENCE() {
		return localizer.localize(localizableILLEGAL_UTF_8_SEQUENCE());
	}

	public static Localizable localizableSOCKET_NOT_CONNECTED() {
		return messageFactory.getMessage("socket.not.connected");
	}

	/**
	 * Socket is not connected.
	 * 
	 */
	public static String SOCKET_NOT_CONNECTED() {
		return localizer.localize(localizableSOCKET_NOT_CONNECTED());
	}

	public static Localizable localizableMESSAGE_HANDLER_PARTIAL_INVALID_TYPE(Object arg0) {
		return messageFactory.getMessage("message.handler.partial.invalid.type", arg0);
	}

	/**
	 * Partial MessageHandler can''t be of type: {0}.
	 * 
	 */
	public static String MESSAGE_HANDLER_PARTIAL_INVALID_TYPE(Object arg0) {
		return localizer.localize(localizableMESSAGE_HANDLER_PARTIAL_INVALID_TYPE(arg0));
	}

	public static Localizable localizablePROVIDER_NOT_FOUND(Object arg0, Object arg1) {
		return messageFactory.getMessage("provider.not.found", arg0, arg1);
	}

	/**
	 * The class {0} implementing the provider {1} is not found. The provider
	 * implementation is ignored.
	 * 
	 */
	public static String PROVIDER_NOT_FOUND(Object arg0, Object arg1) {
		return localizer.localize(localizablePROVIDER_NOT_FOUND(arg0, arg1));
	}

	public static Localizable localizableORIGIN_NOT_VERIFIED() {
		return messageFactory.getMessage("origin.not.verified");
	}

	/**
	 * Origin not verified.
	 * 
	 */
	public static String ORIGIN_NOT_VERIFIED() {
		return localizer.localize(localizableORIGIN_NOT_VERIFIED());
	}

	public static Localizable localizableMESSAGE_HANDLER_WHOLE_OR_PARTIAL() {
		return messageFactory.getMessage("message.handler.whole.or.partial");
	}

	/**
	 * MessageHandler must implement MessageHandler.Whole or MessageHandler.Partial.
	 * 
	 */
	public static String MESSAGE_HANDLER_WHOLE_OR_PARTIAL() {
		return localizer.localize(localizableMESSAGE_HANDLER_WHOLE_OR_PARTIAL());
	}

	public static Localizable localizablePARTIAL_BINARY_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return messageFactory.getMessage("partial.binary.message.out.of.order", arg0);
	}

	/**
	 * Partial binary message received out of order. Session: ''{0}''.
	 * 
	 */
	public static String PARTIAL_BINARY_MESSAGE_OUT_OF_ORDER(Object arg0) {
		return localizer.localize(localizablePARTIAL_BINARY_MESSAGE_OUT_OF_ORDER(arg0));
	}

	public static Localizable localizableMAX_SESSIONS_PER_ENDPOINT_EXCEEDED() {
		return messageFactory.getMessage("max.sessions.per.endpoint.exceeded");
	}

	/**
	 * Maximal number of open sessions per endpoint exceeded.
	 * 
	 */
	public static String MAX_SESSIONS_PER_ENDPOINT_EXCEEDED() {
		return localizer.localize(localizableMAX_SESSIONS_PER_ENDPOINT_EXCEEDED());
	}

	public static Localizable localizableCLIENT_CANNOT_CONNECT(Object arg0) {
		return messageFactory.getMessage("client.cannot.connect", arg0);
	}

	/**
	 * Client cannot connect to "{0}".
	 * 
	 */
	public static String CLIENT_CANNOT_CONNECT(Object arg0) {
		return localizer.localize(localizableCLIENT_CANNOT_CONNECT(arg0));
	}

	public static Localizable localizableFRAME_WRITE_CANCELLED() {
		return messageFactory.getMessage("frame.write.cancelled");
	}

	/**
	 * Frame writing was canceled.
	 * 
	 */
	public static String FRAME_WRITE_CANCELLED() {
		return localizer.localize(localizableFRAME_WRITE_CANCELLED());
	}

}
