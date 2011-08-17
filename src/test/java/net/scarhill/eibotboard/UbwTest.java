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

import net.scarhill.eibotboard.UbwCommand.TimerListener;
import net.scarhill.eibotboard.UbwCommand.TimerMode;

import static org.junit.Assert.assertTrue;
import org.junit.Test;


/**
 * @author jancona
 *
 */
public class UbwTest {
    private int count = 0;
    @Test
    public void testTimerReadInputs() throws InterruptedException {
        count = 0;
        TimerListener listener = new TimerListener() {
            @Override
            public void timerResponse(TimerMode mode, int... value) {
                System.out.println("mode: " + mode + ", values: ");
                for (int v : value) {
                    System.out.print(v + " ");
                }
                System.out.println();
                count++;
            }
            
        };
        
        Ebb ebb = new Ebb("/dev/tty.usbmodemfa131");
        ebb.timerReadInputs(1000, TimerMode.DIGITAL, listener);
        Thread.sleep(5500);
        assertTrue(count > 5);
        ebb.timerReadInputs(0, TimerMode.DIGITAL, null);
        
        ebb.close();
    }

}
