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