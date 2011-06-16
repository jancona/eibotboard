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
package net.scarhill.eibotboard;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * @author jancona
 *
 */
public class EbbTest {
    @Test
    public void testNodeCounter() {
        Ebb ebb = new Ebb("/dev/tty.usbmodemfa131");
        ebb.setNodeCount(12345L);
        assertEquals(12345L, ebb.queryNodeCount());
        ebb.nodeCountIncrement();
        assertEquals(12346L, ebb.queryNodeCount());
        ebb.nodeCountDecrement();
        assertEquals(12345L, ebb.queryNodeCount());
        ebb.close();
    }
    @Test
    public void testStepperMotor1() throws InterruptedException {
        Ebb ebb = new Ebb("/dev/tty.usbmodemfa131");
        // full steps
        ebb.enableMotor(5, 0);
        // one revolution in 1 sec
        ebb.stepperMotorMove(1000, 200, 0);
        Thread.sleep(1000l);
        // reverse one revolution
        ebb.stepperMotorMove(1000, -200, 0);
        Thread.sleep(1000l);
        // ten revolutions
        ebb.stepperMotorMove(5000, 2000, 0);
        Thread.sleep(10000l);
        ebb.enableMotor(0, 0);
        ebb.close();
    }
//    @Test
//    public void testLib() throws Exception {
//        SerialPort serialPort;
//        CommPort commPort = CommPortIdentifier.getPortIdentifier("/dev/tty.usbmodemfa131").open(this.getClass().getName(), 2000);
//        if (commPort instanceof SerialPort) {
//            serialPort = (SerialPort) commPort;
//            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_7,
//                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//            InputStream is = serialPort.getInputStream();
//            BufferedOutputStream out = new BufferedOutputStream(serialPort.getOutputStream());
//            out.write("QN\r\n".getBytes());
//            out.flush();
//            int c;
//            while ((c = is.read()) != -1) {
//                System.out.print((char)c);
//            }
//        } 
//
//    }

}
