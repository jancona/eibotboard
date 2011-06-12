package com.anconafamily.eibotboard;

public interface EbbCommand extends UbwCommand {
	/**
	 * 
	 * @return An array of two ints:
	 * <ul>
	 * <li> <code>REF_RA0_VOLTAGE</code> is the voltage on the REF_RA0 net, as expressed as a 10 bit number where 0 = 0.0V and 1023 = 3.3V
	 * <li> <code>V+_NET_VOLTAGE</code> is the voltage on the V+ net scaled by the resistor divider R13 and R18
	 * </ul>
	 */
	int[] queryCurrent();
	/**
	 * Increment the 32 bit Node Counter by 1.
	 */
	void nodeCountIncrement();
	/**
	 * Decrement the 32 bit Node Counter by 1.
	 */
	void nodeCountDecrement();

	/**
	 * Turn off interrupts and jump into the bootloader. The EBB will enter
	 * bootloader mode without having to push any buttons. Note that this
	 * command will ONLY work if you have a EBB bootloader version later than
	 * 7/3/2010 (the version released on 7/3/2010 has a distinct LED blink mode
	 * - the USB LED stays on 3 times longer than the USR LED). With a previous
	 * version of the bootloader code, this command may cause the EBB to be come
	 * unresponsive.
	 */
	void bootLoad();
	/**
	 * Set the Node Counter to <code>value</code>. 
	 * @param value a 32 bit unsigned int.
	 */
	void setNodeCount(long value);
	/**
	 * Set the value of the Layer variable.
	 * @param layer a single unsigned byte
	 */
	void setLayer(int layer);
	/**
	 * Report back the current value of the Layer variable.
	 * @return a single unsigned byte
	 */
	int queryLayer();
	/**
	 * current value of the Node Counter
	 * @return unsigned long int (8 bytes) value that gets incremented each time an SM command is finished 
	 */
	long queryNodeCount();
	/**
	 * @return <code>true</code>  if the PRG button has been pressed since the last QB command, <code>false</code> if it has not
	 */
	boolean queryButton();
	/**
	 * @return <code>true</code>  if the pen is up, <code>false</code> if it is down
	 */
	boolean queryPen();
	/**
	 * toggles the state of the pen (up->down and down->up) for the default duration of 500 milliseconds.
	 */
	void togglePen();

	/**
	 * toggles the state of the pen (up->down and down->up).
	 * 
	 * @param duration
	 *            a value from 1 to 65,535 and is in milliseconds. It represents
	 *            the total length of time between when the pen move is started,
	 *            and when the next command will be executed. Note that this
	 *            does not have anything to do with how fast the pen moves
	 *            (which is set with the SC command). This parameter
	 *            is to force the EBB not to execute the next command (normally
	 *            a stepperMotorMove) for some length of time, which gives the system time to
	 *            allow the pen move to complete and then some extra time before
	 *            moving the motors (if you set up the pen speed and this
	 *            duration parameter properly).
	 */
	void togglePen(int duration);

	/**
	 * Make the motors draw a straight line at constant velocity. The maximum
	 * speed that the EBB can generate is 25,000 steps/s. It is helpful to use
	 * this command with zeros for both <axis> parameters after moving the pen
	 * up or down to give it time to finish moving before starting the next
	 * motor move. If both axis1 and axis2 are zero, then a delay of <duration>
	 * ms is executed.
	 * 
	 * @param duration
	 *            value from 1 to 65,535 and is in milliseconds. It represents
	 *            the total length of time you want this move to take.
	 * @param axis1
	 *            value from -32,767 to +32,767 and represent the number of
	 *            steps for motor 1 to take in duration milliseconds.
	 * @param axis2
	 *            value from -32,767 to +32,767 and represent the number of
	 *            steps for motor 2 to take in duration milliseconds.
	 */
	void stepperMotorMove(int duration, int axis1, int axis2);

	/**
	 * Make the pen go up or down. On EBB versions 1.1, 1.2 and 1.3 this will
	 * make the solenoid output turn on and off. On all EBB versions it will
	 * also make the RC servo output on RB1 move to the up or down position.
	 * This version applies a default duration of 500 ms.
	 * 
	 * @param state
	 *            <code>true</code> sets the pen down, <code>false</code> sets
	 *            it up
	 */
	void setPenState(boolean state);

	/**
	 * Make the pen go up or down. On EBB versions 1.1, 1.2 and 1.3 this will
	 * make the solenoid output turn on and off. On all EBB versions it will
	 * also make the RC servo output on RB1 move to the up or down position.
	 * 
	 * @param state
	 *            <code>true</code> sets the pen down, <code>false</code> sets
	 *            it up
	 * @param duration
	 *            value from 1 to 65,535 and is in milliseconds. It represents
	 *            the total length of time between when the pen move is started,
	 *            and when the next command will be executed. Note that this
	 *            does not have anything to do with how fast the pen moves
	 *            (which is set with the SC command). The duration parameter is
	 *            to force the EBB not to execute the next command (normally an
	 *            SM) for some length of time, which gives the system time to
	 *            allow the pen move to complete and then some extra time before
	 *            moving the motors (if you set up the pen speed and this
	 *            duration parameter properly).
	 */
	void setPenState(boolean state, int duration);

	/**
	 * To enable a motor driver, set its Enable parameter to 1. <br>
	 * To disable a motor driver, set its Enable parameter to 0. <br>
	 * For example, "EM,1,0" will enable motor 1 and disable motor 2.<br>
	 * To set the microstep mode of BOTH motor drivers (the same signals go to
	 * both drivers, so you can't set them separately) use a value of 1,2,3,4 or
	 * 5 for Enable1. When you use a value of 1,2,3,4 or 5 for Enable1, the
	 * Enable2 parameter is not needed.<br>
	 * When setting microstep values with Enable1:<br>
	 * 1 will enable both axis in 1/16th step mode (default on boot)<br>
	 * 2 will enable both axis in 1/8th step mode<br>
	 * 3 will enable both axis in 1/4 step mode<br>
	 * 4 will enable both axis in 1/2 step mode<br>
	 * 5 will enable both axis in full step mode<br>
	 * Note that any time an SM command is executed, both motors become
	 * 'enabled' before the move starts. Thus it is almost never necessary to
	 * issue a "EM,1,1" command to enable both motors.
	 */
	void enableMotor(int motor1, int motor2);

	/**
	 * set different parameters that effect the operation of the EBB.<br>
	 * 
	 * SC,1,0 - uses just the solenoid output for pen up/down<br>
	 * SC,1,2 - uses solenoid and RB1 RC servo output for pen up/down, hardware
	 * RC output (default)<br>
	 * SC,2,0 - use built in driver chips for motor control (default)<br>
	 * SC,2,1 - use the following pins for stepper motor control - EBB v1.1
	 * only:
	 * <ul>
	 * <li>ENABLE1 = RA5
	 * <li>ENABLE2 = RB5
	 * <li>STEP1 = RD1
	 * <li>DIR1 = RD0
	 * <li>STEP2 = RC2
	 * <li>DIR2 = RC0
	 * </ul>
	 * SC,4,<servo_min> - sets the minimum value for the servo (1 to 65535)<br>
	 * SC,5,<servo_max> - sets the maximum value for the servo (1 to 65535)<br>
	 * SC,10,<servo_rate> - sets the rate of change of the servo (when going
	 * up/down)<br>
	 * SC,11,<servo_rate_up> - sets the rate of change of the servo when going
	 * up (new in v1.9.2)<br>
	 * SC,12,<servo_rate_down> - sets the rate of change of the servo when going
	 * down (new in v1.9.2)<br>
	 * SC,13,<use_alt_pause> - turns on (1) or off (0) alternate pause button
	 * function on RB0. On by default. For v1.1 boards, it uses RB2 instead.
	 * (new in v2.0)
	 */
	void stepperModeConfigure(int value1, int value2);

	/**
	 * a generic RC servo output command. It allows you to control the RC servo
	 * output system on the EBB. Many I/O pins have RPx numbers (see the
	 * schematic) - and you can output RC servo pulses on up to 7 of these RPx
	 * pins. Any of them that you want - it doesn't matter to the software.
	 * There are seven RC servo 'channels', which have no physcial meaning other
	 * than we can only output up to 7 separate signals. But you can assign any
	 * RPx pin to any channel - that's up to you to manage. The RC servo system
	 * will cycle through each of the 7 channels. Each gets 3m of 'time'. (Thus
	 * giving a 21ms repeat rate for the whole RC system.) If the current
	 * channel is enabled, then at the beginning of its 3ms time slot, it's RPx
	 * pin is set high. Then, <duration> time later, the RPx pin is set low.
	 * This time is controlled purely by hardware (the ECCP2 in the CPU) so
	 * there is no jitter for the pulse durations. <duration> is in units of
	 * 1/12,000 of a second, so 32,000 for <duration> would be exactly 3ms.
	 * 
	 * 
	 * @param channel
	 *            a number from 0 to 7 (with 0 as a special value, see below)
	 * @param duration
	 *            a number from 0 to 32,000, and represents the 'on' time (0 =
	 *            0ms, 32,000 = 3ms)
	 * @param output_pin
	 *            a number from 0 to 24, and is the RPx pin number you want this
	 *            channel to come out on- see schematic for RPx numbers
	 * @param rate
	 *            the rate at which to change from the current value to the new
	 *            value
	 */
	void rcServoOutput(int channel, int duration, int output_pin, int rate);
}
