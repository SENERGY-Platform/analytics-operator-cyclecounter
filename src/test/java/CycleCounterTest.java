import org.infai.seits.sepl.operators.Message;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.List;

public class CycleCounterTest {
    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Test
    public void run() throws Exception{
        environmentVariables.set("CONFIG", "{\"config\":{\"MinDuration\":\"60000\"}}");
        CycleCounter cycleCounter = new CycleCounter();
        List<Message> messages = TestMessageProvider.getTestMesssagesSet();
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            m.addInput("timestampAssume");
            DateTimeUtils.setCurrentMillisFixed(DateParser.parseDateMills(m.getInput("timestampAssume").getString()));
            cycleCounter.config(m);
            cycleCounter.run(m);
            try {
                m.addInput("DayCounter");
                double actual = Double.parseDouble(m.getMessageString().split("DayCounter\":")[1].split(",")[0]);
                double expected = m.getInput("DayCounter").getValue();
                Assert.assertEquals(expected, actual, 0.1);
            }catch (NullPointerException|IndexOutOfBoundsException e){
                System.out.println("Skipped test for DayCounter because no expected values were provided.");
            } catch(NumberFormatException e) {
                Assert.fail("Failed test: Operator did not count Day correctly");
            }
            try {
                m.addInput("MonthCounter");
                double actual = Double.parseDouble(m.getMessageString().split("MonthCounter\":")[1].split(",")[0]);
                double expected = m.getInput("MonthCounter").getValue();
                Assert.assertEquals(expected, actual, 0.1);
            }catch (NullPointerException|IndexOutOfBoundsException e){
                System.out.println("Skipped test for MonthCounter because no expected values were provided.");
            } catch(NumberFormatException e) {
                Assert.fail("Failed test: Operator did not count Month correctly");
            }
            try {
                m.addInput("YearCounter");
                double actual = Double.parseDouble(m.getMessageString().split("YearCounter\":")[1].split(",")[0]);
                double expected = m.getInput("YearCounter").getValue();
                Assert.assertEquals(expected, actual, 0.1);
            }catch (NullPointerException|IndexOutOfBoundsException e){
                System.out.println("Skipped test for YearCounter because no expected values were provided.");
            } catch(NumberFormatException e) {
                Assert.fail("Failed test: Operator did not count Year correctly");
            }
            try {
                m.addInput("DayTimestamp");
                String actual = m.getMessageString().split("DayTimestamp\":\"")[1].split("\"")[0];
                String expected = m.getInput("DayTimestamp").getString();
                Assert.assertEquals(expected, actual);
            }catch (NullPointerException|IndexOutOfBoundsException e){
                System.out.println("Skipped test for DayTimestamp because no expected values were provided.");
            } catch(NumberFormatException e) {
                Assert.fail("Failed test: Operator did not calculate DayTimestamp correctly");
            }
            try {
                m.addInput("MonthTimestamp");
                String actual = m.getMessageString().split("MonthTimestamp\":\"")[1].split("\"")[0];
                String expected = m.getInput("MonthTimestamp").getString();
                Assert.assertEquals(expected, actual);
            }catch (NullPointerException|IndexOutOfBoundsException e){
                System.out.println("Skipped test for MonthTimestamp because no expected values were provided.");
            } catch(NumberFormatException e) {
                Assert.fail("Failed test: Operator did not calculate MonthTimestamp correctly");
            }try {
                m.addInput("YearTimestamp");
                String actual = m.getMessageString().split("YearTimestamp\":\"")[1].split("\"")[0];
                String expected = m.getInput("YearTimestamp").getString();
                Assert.assertEquals(expected, actual);
            }catch (NullPointerException|IndexOutOfBoundsException e){
                System.out.println("Skipped test for YearTimestamp because no expected values were provided.");
            } catch(NumberFormatException e) {
                Assert.fail("Failed test: Operator did not calculate YearTimestamp correctly");
            }
            System.out.println("Finished tests for message " + i);
        }

    }

}
