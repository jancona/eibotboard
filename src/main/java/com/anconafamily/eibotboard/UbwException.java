// Copyright (c) 2011 James Ancona. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.anconafamily.eibotboard;


/**
 * Exception that wraps the returned error status from the UBW/EBB
 * 
 * Note that errors other than TX_BUFFER_OVERRUN and RX_BUFFER_OVERRUN, probably indicate an API bug
 * 
 * @author Jim Ancona jim@anconafamily.com
 */
public class UbwException extends RuntimeException {
	enum ErrorCode {
		UNUSED_1("!0"),
		UNUSED_2("!1"),
		TX_BUFFER_OVERRUN("!2"),
		RX_BUFFER_OVERRUN("!3"),
		MISSING_PARAMETER("!4"),
		NEED_COMMA_NEXT("!5"),
		INVALID_PARAMETER_VALUE("!6"),
		EXTRA_PARAMETER("!7"),
		UNKNOWN_COMMAND("!8"), 
		COMM_ERROR("CE"), 
		RESPONSE_ERROR("RE");

		private String stringValue;
		private ErrorCode(String stringValue) {
			this.stringValue = stringValue;
		}
		public String stringValue() {
			return stringValue;
		}
		public static ErrorCode fromStringValue(String value) {
			for (ErrorCode errorCode : ErrorCode.class.getEnumConstants()) {
				if (errorCode.stringValue().equals(value))
					return errorCode;
			}
			return null;
		}
	}

	private ErrorCode errorCode;
	
	public UbwException(String message) {
		super(message.substring(message.indexOf(' ') + 1));
		errorCode = ErrorCode.fromStringValue(message.substring(0, message.indexOf(' ')));
	}
	public UbwException(String message, Throwable t, ErrorCode ec) {
		super(message, t);
		errorCode = ec;
	}
	public UbwException(String message, ErrorCode ec) {
		super(message);
		errorCode = ec;
	}
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
