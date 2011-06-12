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

public interface UbwCommand {
	/**
	 * Set the state of the port direction registers for ports A, B and C, as well as enable 
	 * analog inputs. This allows you to turn each pin into an input or an output on a pin by 
	 * pin basis, or enable one or more of the pins to be analog inputs.
	 */
	void configure(int dirA, int dirB, int dirC, int AnalogEnableCount);
	/**
	 * Take the values you give it and write them to the port A, B and C data registers. This 
	 * allows you to set the state of all pins that are outputs.
	 */
	void outputState(int portA, int portB, int portC);
	/**
	 * When you send the UBW an "I" command, it will respond with an "I" packet back that will 
	 * hold the value of each bit in each of the three ports A, B and C. It reads the state of 
	 * the pin, no matter if the pin is an input or an output. If the pin is configured as an 
	 * analog input, the bit will always read low (0) in the "I" packet.
	 * 
	 * @return an array of three status values between 0 and 255
	 */
	int[] inputState();
	/**
	 * @return a version string like "UBW FW D Version 1.4.0"
	 */
	String version();
	/**
	 * Reset to default state.
	 */
	void reset();
	/**
	 * Set the delay for one of two timers. When the timer times out, it will cause "I" packet or "A" 
	 * packet responses to be asynchronously returned. 
	 */
	void timerReadInputs(int timeBetweenPacketsInMilliseconds, TimerMode mode, TimerListener listener);
	
	
	/**
	 * @return the last sampled set of analog inputs. All enabled analog inputs are sampled every 1ms, 
	 * and stored. Whenever an "A" packet is received, the latest stored value for the analog inputs is returned.<br>
	 * Example return value: {145,1004,0000,0045} (The return packet would look like this if there were 4 analog 
	 * inputs enabled with the "C" command). There can be up to 12 analog input enabled, and thus there might be up to 12 elements in the array.
	 * 
	 */
	int[] sampleAnalogInputs();
	
	/**
	 * Read a value from memory
	 * @param address the address to read (between 0 and 4095)
	 * @return the value in <tt>address</tt> (between 0 and 255)
	 */
	int memoryRead(int address);

	/**
	 * Write a value to memory
	 * @param address the address to write (between 0 and 4095)
	 * @param value the value to write (between 0 and 255)
	 */
	void memoryWrite(int address, int value);
	
	/**
	 * Set the direction on just one pin at a time. (Input or Output)
	 * @param port The port to change (A, B or C).
	 * @param pin The pin in the port you want to change the direction on.
	 * @param direction The direction (INPUT or OUTPUT)
	 */
	void pinDirection(Port port, int pin, PinDirection direction);
	
	/**
	 * Read the state of just one pin at a time. 
	 * 
	 * @param port The port to change (A, B or C).
	 * @param pin The pin in the port you want to change the direction on.
	 * @return <tt>true</tt> = HIGH, <tt>false></tt> = LOW
	 */
	boolean pinInput(Port port, int pin);
	
	/**
	 * Set  the output value (if it is currently set to be an output) on just one pin at a time. 
	 * 
	 * @param port The port to change (A, B or C).
	 * @param pin The pin in the port you want to change the direction on.
	 * @param value <tt>true</tt> = HIGH, <tt>false></tt> = LOW
	 */
	void pinOutput(Port port, int pin, boolean value);
	
	/**
	 * 
	 * @param parameter an unsigned 8 bit value, representing the parameter number you wish to change.
	 * <table>
	 * <tr><th>Parameter</th><th>Value</th><th>Meaning</th></tr>
	 * <tr><td>1</td><td>0 or 1</td><td>0 = Turn off "OK" packets<br>1 = Turn on "OK" packets (default)</td></tr>
	 * </table>
	 * @param value a value who's meaning depends upon the <code>parameter<code> number chosen
	 */
	void configure(int parameter, int value);
	
	/**
	 * turn any pin into an RC servo output, if that pin is already configured as a digital output.
	 * @param port the port to set
	 * @param pin a number between and including 0 to 7. It indicates which pin in the port for which you want 
	 * to set the state. Note that some pins do not come out of the chip (RA6, RA7, RC3, RC4 and RC5), and some 
	 * pins are not accessible via headers on the SparkFun version of UBW (RC0, RC1, RC2). You can still set RC 
	 * outputs on those pins, but the non-existent ones will just be skipped by the RC code, and if you set RC 
	 * outputs on RC0, RC1 or RC2, you may see interesting results (since RC0 and RC1 have LEDs on them).
	 * @param value  a value between 0 and 11890. A <code>Value</code> of 0 (zero) will turn the RC output (for that pin) 
	 * completely off. A <code>Value</code> of 1 will cause a 1ms high pulse on the pin. A <code>Value</code> of 11890 
	 * will cause a 2ms high pulse on the pin. Any <code>Value</code> in-between 1 and 11890 will cause a high pulse 
	 * whose duration is proportionally between 1ms and 2ms. These pulses repeat every 19ms.
	 */
	void rcServoOutput(Port port, int pin, int value);
	
	/**
	 * 
	 * @param init
	 * @param waitMask
	 * @param waitDelay
	 * @param strobeMask
	 * @param strobeDelay
	 */
	void bulkConfigure(int init, int waitMask, int waitDelay, int strobeMask, int strobeDelay);
	/**
	 * 
	 * @param byteStream
	 */
	void bulkOutput(byte[] byteStream);
	/**
	 * 
	 * @param hexBytes
	 */
	void bulkStream(byte[] byteStream);
	
	enum TimerMode {
		DIGITAL(0), 
		ANALOG(1);
		
		private int intValue;
	
		private TimerMode(int intValue) {
			this.intValue = intValue;
		}
		public int intValue() {
			return intValue;
		}
	}
	interface TimerListener {
		void timerResponse(int value);
	}
	enum Port {A, B, C}
	enum PinDirection {
		OUTPUT(0), INPUT(1);
		private int intValue;
		
		private PinDirection(int intValue) {
			this.intValue = intValue;
		}
		public int intValue() {
			return intValue;
		}
	}
	
}
