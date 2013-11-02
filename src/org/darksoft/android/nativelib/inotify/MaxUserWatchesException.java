/*
 *  This file is part of Inofity Java Interface
 *  MaxUserWatchesException.java - Class for Inotify Error.
 *  Copyright (C) 2012  Joel Peláez Jorge <joelpelaez@gmail.com>
 * 
 *  Inofity Java Interface is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Inofity Java Interface Control is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Inofity Java Interface.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.darksoft.android.nativelib.inotify;

public class MaxUserWatchesException extends Exception {

	private static final long serialVersionUID = -7876794221685768289L;
	
	public MaxUserWatchesException() {
		super();
	}
	public MaxUserWatchesException(String message) {
		super(message);
	}
	public MaxUserWatchesException(String message, Throwable cause) {
		super(message, cause);
	}
	public MaxUserWatchesException(Throwable cause) {
		super(cause);
	}
}
