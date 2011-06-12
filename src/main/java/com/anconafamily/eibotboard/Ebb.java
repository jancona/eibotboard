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

public class Ebb extends Ubw implements EbbCommand {

	public Ebb(String port) {
		super(port);
	}

	@Override
	public int[] queryCurrent() {
		execute("QC");
		String[] response = readResponse().split(",");
		int[] ret = new int[2];
		int i = 0;
		for (String r : response) {
			ret[i++] = Integer.parseInt(r);
		}
		readResponse();
		return ret;
	}

	@Override
	public void nodeCountIncrement() {
		execute("NI");
		readResponse();
	}

	@Override
	public void nodeCountDecrement() {
		execute("ND");
		readResponse();
	}

	@Override
	public void bootLoad() {
		execute("BL");
		readResponse();
	}

	@Override
	public void setNodeCount(long value) {
		validateRange(value, 0L, ((long) 1 << 32) - 1L);
		execute("SN," + value);
		readResponse();
	}

	@Override
	public void setLayer(int layer) {
		validateByte(layer);
		execute("SL," + layer);
		readResponse();
	}

	@Override
	public int queryLayer() {
		execute("QL");
		String s = readResponse();
		readResponse();
		return Integer.parseInt(s);
	}

	@Override
	public long queryNodeCount() {
		// Note: this will fail for unsigned values larger than Long.MAX_VALUE
		execute("QN");
		String s = readResponse();
		readResponse();
		return Long.parseLong(s);
	}

	@Override
	public boolean queryButton() {
		execute("QB");
		String s = readResponse();
		readResponse();
		return s.equals("1");
	}

	@Override
	public boolean queryPen() {
		execute("QP");
		String s = readResponse();
		readResponse();
		return s.equals("1");
	}

	@Override
	public void togglePen() {
		execute("TP");
		readResponse();
	}

	@Override
	public void togglePen(int duration) {
		validateRange(duration, 0, 65535);
		execute("TP," + duration);
		readResponse();
	}

	@Override
	public void stepperMotorMove(int duration, int axis1, int axis2) {
		validateRange(duration, 0, 65535);
		validateRange(axis1, -32767, 32767);
		validateRange(axis2, -32767, 32767);
		execute("SM," + duration + "," + axis1 + "," + axis2);
		readResponse();
	}

	@Override
	public void setPenState(boolean state) {
		execute("SP," + (state ? "1" : "0"));
		readResponse();
	}
	@Override
	public void setPenState(boolean state, int duration) {
		validateRange(duration, 0, 65535);
		execute("SP," + (state ? "1," : "0,") + duration);
		readResponse();
	}

	@Override
	public void enableMotor(int motor1, int motor2) {
		validateRange(motor1, 0, 5);
		validateRange(motor2, 0, 1);
		if (motor1 > 1)
			execute("EM," + motor1);
		else
			execute("EM," + motor1 + "," + motor2);
		readResponse();
	}

	@Override
	public void stepperModeConfigure(int value1, int value2) {
		validateByte(value1);
		validateRange(value2, 0, 65535);
		execute("SC," + value1 + "," + value2);
		readResponse();
	}

	@Override
	public void rcServoOutput(int channel, int duration, int output_pin, int rate) {
		validateRange(channel, 0, 7);
		validateRange(duration, 0, 32000);
		validateRange(output_pin, 0, 24);
		execute("S2," + channel + "," + duration + "," + output_pin + "," + rate);
		readResponse();
	}

}
