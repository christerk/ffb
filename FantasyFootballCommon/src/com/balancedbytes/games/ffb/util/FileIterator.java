package com.balancedbytes.games.ffb.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Utility class for a breadth-first iteration through filtered files and/or
 * directories of a directory structure.
 * 
 * @author Kalimar
 */
public class FileIterator implements Iterator<File> {

	/** FileFilter that filters nothing */
	public static FileFilter ACCEPT_ALL = new FileFilter() {
		public boolean accept(File pathname) {
			return true;
		}
	};

	private File fStartDirectory;
	private FileFilter fFileFilter;
	private boolean fIncludeDirectories;

	private List<File> fFileList;
	private Iterator<File> fFileIterator;
	private List<File> fDirectoryList;
	private int fKnownSize;

	/**
	 * Convenience Constructor. Iterator returns files and directories unfiltered.
	 * 
	 * @param pStartDirectory directory structure starting point.
	 */
	public FileIterator(File pStartDirectory) {
		this(pStartDirectory, true, null);
	}

	/**
	 * Convenience Constructor. Iterator returns files (or files and directories)
	 * unfiltered.
	 * 
	 * @param pStartDirectory     directory structure starting point.
	 * @param pIncludeDirectories whether directories should be returned as well
	 */
	public FileIterator(File pStartDirectory, boolean pIncludeDirectories) {
		this(pStartDirectory, pIncludeDirectories, null);
	}

	/**
	 * Default Constructor.
	 * 
	 * @param pStartDirectory     directory structure starting point.
	 * @param pIncludeDirectories whether directories should be returned as well
	 * @param pFileFilter         filter to be applied to all files and directories,
	 *                            determines which files the iterator will return.
	 */
	public FileIterator(File pStartDirectory, boolean pIncludeDirectories, FileFilter pFileFilter) {
		if (pFileFilter == null) {
			setFileFilter(ACCEPT_ALL);
		} else {
			setFileFilter(pFileFilter);
		}
		setIncludeDirectories(pIncludeDirectories);
		setStartDirectory(pStartDirectory);
		reset();
	}

	/**
	 * Descends one level by visiting all directories on the current level and
	 * collecting files and directories. Called whenever all the files on the
	 * current level have been returned by the iterator.
	 */
	protected void descend() {
		List<File> nextLevelFiles = new LinkedList<File>();
		List<File> nextLevelDirectories = new LinkedList<File>();
		Iterator<File> directoryIterator = fDirectoryList.iterator();
		while (directoryIterator.hasNext()) {
			File directory = (File) directoryIterator.next();
			if (fIncludeDirectories) {
				nextLevelFiles.add(directory);
			}
			File[] files = directory.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						if (fFileFilter.accept(files[i])) {
							nextLevelFiles.add(files[i]);
						}
					} else if (files[i].isDirectory()) {
						nextLevelDirectories.add(files[i]);
					}
				}
			}
		}
		fFileList = nextLevelFiles;
		fKnownSize += nextLevelFiles.size();
		fDirectoryList = nextLevelDirectories;
		fFileIterator = fFileList.iterator();
	}

	/**
	 * Accessor to the used File Filter.
	 */
	public FileFilter getFileFilter() {
		return fFileFilter;
	}

	/**
	 * Accessor to the directory structure starting point.
	 */
	public File getStartDirectory() {
		return fStartDirectory;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements.
	 * 
	 * @see Iterator#hasNext()
	 */
	public boolean hasNext() {
		while ((fFileIterator == null) || (!fFileIterator.hasNext() && (fDirectoryList.size() > 0))) {
			descend();
		}
		return fFileIterator.hasNext();
	}

	/**
	 * Returns the includeDirectories.
	 * 
	 * @return boolean
	 */
	public boolean hasIncludeDirectories() {
		return fIncludeDirectories;
	}

	/**
	 * Returns the size as currently known.
	 */
	public int knownSize() {
		return fKnownSize;
	}

	/**
	 * Returns the next element in the interation.
	 * 
	 * @see Iterator#next()
	 */
	public File next() {
		if (hasNext()) {
			return fFileIterator.next();
		} else {
			throw new NoSuchElementException("No more files available.");
		}
	}

	/**
	 * Removes from the underlying collection the last element returned by the
	 * iterator (unsupported operation).
	 * 
	 * @see Iterator#remove()
	 * @exception UnsupportedOperationException always
	 */
	public void remove() {
		throw new UnsupportedOperationException("Removing of files unsopported.");
	}

	/**
	 * Resets the iterator (same effect as new).
	 */
	public void reset() {
		fDirectoryList = new LinkedList<File>();
		fDirectoryList.add(fStartDirectory);
		fKnownSize = 0;
		hasNext();
	}

	/**
	 * Defines the used File Filter.
	 */
	protected void setFileFilter(FileFilter pFileFilter) {
		fFileFilter = pFileFilter;
	}

	/**
	 * Sets the includeDirectories.
	 * 
	 * @param pIncludeDirectories The includeDirectories to set
	 */
	protected void setIncludeDirectories(boolean pIncludeDirectories) {
		fIncludeDirectories = pIncludeDirectories;
	}

	/**
	 * Defines the directory structure starting point.
	 * 
	 * @exception IllegalArgumentException if the given File is not a directory.
	 */
	protected void setStartDirectory(File pStartDirectory) {
		if (!pStartDirectory.isDirectory()) {
			throw new IllegalArgumentException("File " + pStartDirectory.getPath() + " is not a directory.");
		}
		fStartDirectory = pStartDirectory;
	}

	/**
	 * Simple Test for this class. Lists all files of the given directory structure
	 * on <code>stdout</code>.
	 */
	public static void main(String[] args) {
		int knownSize = 0;
		FileIterator myIterator = new FileIterator(new File(args[0]));
		while (myIterator.hasNext()) {
			if (myIterator.fKnownSize != knownSize) {
				knownSize = myIterator.knownSize();
				System.out.println("known size: " + knownSize);
			}
			System.out.println(myIterator.next());
		}
		System.out.println("known size: " + myIterator.knownSize());
	}

}
