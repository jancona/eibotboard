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

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;

public class Ubw implements UbwCommand {
	private static final Charset ASCII = Charset.forName("US-ASCII");
	private SerialPort serialPort = null;
	private BufferedReader in;
	private BufferedOutputStream out;
	private TimerListener timerListener;

	public Ubw() {
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            if (com.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	try {
            		connect(com);
        		} catch (Exception e) {
        			throw new UbwException("Error opening port: " + com.getName(), e, UbwException.ErrorCode.COMM_ERROR);
        		}
            }
        }
        if (serialPort == null)
        	throw new UbwException("No serial port found to open", UbwException.ErrorCode.COMM_ERROR);
	}
	public Ubw(String port) {
		try {
			connect(CommPortIdentifier.getPortIdentifier(port));
		} catch (UbwException e) {
			throw e;
		} catch (Exception e) {
			throw new UbwException("Error opening port: " + port, e, UbwException.ErrorCode.COMM_ERROR);
		}
	}

	private void connect(CommPortIdentifier portIdentifier) throws NoSuchPortException,
			PortInUseException, UnsupportedCommOperationException, IOException {
		CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
		if (commPort instanceof SerialPort) {
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			in = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			out = new BufferedOutputStream(serialPort.getOutputStream());
		} else {
			throw new UbwException("Port " + portIdentifier.getName() + " is not a serial port", UbwException.ErrorCode.COMM_ERROR);
		}
	}
	
	public void close() {
		try {
			in.close();
		} catch (IOException ex) {}
		try {
			out.close();
		} catch (IOException ex) {}
		serialPort.close();
	}

	@Override
	public void configure(int dirA, int dirB, int dirC, int analogEnableCount) {
		validateByte(dirA);
		validateByte(dirB);
		validateByte(dirC);
		validateRange(analogEnableCount, 0, 13);
		execute("C," + dirA + "," + dirB + "," + dirC + "," + analogEnableCount);
	}

	@Override
	public void outputState(int portA, int portB, int portC) {
		validateByte(portA);
		validateByte(portB);
		validateByte(portC);
		execute("O," + portA + "," + portB + "," + portC);
		readResponse();
	}


	@Override
	public int[] inputState() {
		execute("I");
		String s = readResponse();
		try {
			String[] r = s.split(",");
			int[] response = new int[3];
			response[0] = Integer.parseInt(r[1]);
			response[1] = Integer.parseInt(r[2]);
			response[2] = Integer.parseInt(r[3]);
			return response;
		} catch (RuntimeException e) {
			throw new UbwException("Exception parsing response: '" + s + "'", e, UbwException.ErrorCode.RESPONSE_ERROR);
		}
	}

	@Override
	public String version() {
		execute("V");
		return readResponse();
	}

	@Override
	public void reset() {
		execute("R");
		readResponse();
	}

	@Override
	public void timerReadInputs(int timeBetweenPacketsInMilliseconds,
			TimerMode mode, TimerListener listener) {
		timerListener = listener;
		validateRange(timeBetweenPacketsInMilliseconds, 0, 30000);
		// TODO: Finish this 
	}

	@Override
	public int[] sampleAnalogInputs() {
		execute("R");
		String s = readResponse();
		String[] r = s.split(",");
		int[] response = new int[r.length - 1];
		for (int i = 1; i < r.length; i++) {
			response[i - 1] = Integer.parseInt(r[i]);
		}
		return response;
	}

	@Override
	public int memoryRead(int address) {
		validateRange(address, 0, 4095);
		execute("MR," + address);
		return Integer.parseInt(readResponse().substring(3));
	}

	@Override
	public void memoryWrite(int address, int value) {
		validateRange(address, 0, 4095);
		validateByte(value);
		execute("MW," + address + "," + value);
		readResponse();
	}

	@Override
	public void pinDirection(Port port, int pin, PinDirection direction) {
		validateRange(pin, 0, 7);
		execute("PD," + port + "," + pin + "," + direction.intValue());
		readResponse();
	}

	@Override
	public boolean pinInput(Port port, int pin) {
		validateRange(pin, 0, 7);
		execute("PI," + port + "," + pin);
		return readResponse().substring(3).equals("1");
	}

	@Override
	public void pinOutput(Port port, int pin, boolean value) {
		validateRange(pin, 0, 7);
		execute("PI," + port + "," + pin + "," + (value ? "1" : "0"));
		readResponse();
	}

	@Override
	public void configure(int parameter, int value) {
		validateByte(parameter);
		execute("CU," + parameter + "," + value);
		readResponse();
	}

	@Override
	public void rcServoOutput(Port port, int pin, int value) {
		validateRange(pin, 0, 7);
		validateRange(value, 0, 11890);
		execute("RC," + port + "," + pin + "," + value);
		readResponse();
	}

	@Override
	public void bulkConfigure(int init, int waitMask, int waitDelay, int strobeMask, int strobeDelay) {
		validateByte(strobeMask);
		execute("BC," + init + "," + waitMask + "," + waitDelay + "," + strobeMask + "," + strobeDelay);
		readResponse();
	}

	@Override
	public void bulkOutput(byte[] byteStream) {
		execute("BO," + toHex(byteStream));
		readResponse();
	}
	@Override
	public void bulkStream(byte[] byteStream) {
		try {
			out.write(("BS," + byteStream.length + ",").getBytes(ASCII));
			out.write(byteStream);
			out.write(13);
		} catch (IOException e) {
			throw new UbwException("Exception writing command 'BS," + byteStream.length + ",' to device", e, UbwException.ErrorCode.COMM_ERROR);
		}
		readResponse();
	}


	protected void validateByte(int value) {
		validateRange(value, 0, 255);
	}
	protected void validateRange(long value, long l, long m) {
		if (value < l || value > m) 
			throw new IllegalArgumentException("Value '" + value + "' must be between " + l + " and " + m + ".");
		
	}
	protected void execute(String command) {
		try {
			out.write(command.getBytes(Charset.forName("US-ASCII")));
			out.write(13);
		} catch (IOException e) {
			throw new UbwException("Exception writing command '" + command + "' to device", e, UbwException.ErrorCode.COMM_ERROR);
		}
	}
	protected String readResponse() {
		try {
			return in.readLine();
		} catch (IOException e) {
			throw new UbwException("Exception reading response from device", e, UbwException.ErrorCode.COMM_ERROR);
		}
	}

	static final char[] HEXES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES[(b & 0xF0) >> 4]).append(HEXES[b & 0x0F]);
		}
		return hex.toString();
	}
}
