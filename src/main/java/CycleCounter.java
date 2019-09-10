/*
 * Copyright 2018 InfAI (CC SES)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.infai.seits.sepl.operators.Config;
import org.infai.seits.sepl.operators.Message;
import org.infai.seits.sepl.operators.OperatorInterface;
import org.joda.time.DateTimeUtils;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;


public class CycleCounter implements OperatorInterface {

    protected Double threshold;
    protected long minDuration, lastEvent = 0;
    protected ZoneId timezone;
    List<Long> events;

    public CycleCounter() {
        Config config = new Config();
        threshold = Double.parseDouble(config.getConfigValue("Threshold", "-1"));
        System.out.println("threshold set to: " + threshold);
        minDuration = Long.parseLong(config.getConfigValue("MinDuration", "0"));
        System.out.println("minDuration set to:" + minDuration);
        String configTimezone = config.getConfigValue("Timezone", "+02");
        System.out.println("timezone read as: " + configTimezone);
        timezone = ZoneId.of(configTimezone); //As configured
        events = new ArrayList<>();
    }

    @Override
    public void run(Message message) {
        TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(DateParser.parseDate(message.getInput("timestamp").getString()));
        final long timestampMillis = Instant.from(temporalAccessor).toEpochMilli();

        if(timestampMillis <= lastEvent + minDuration) {
            //Last event not finished
            System.out.println("Skipping value: last event unfinished");
            return;
        }

        final Double value = message.getInput("value").getValue();

        if(value < threshold) {
            //threshold not met
            System.out.println("Skipping value: threshold not met");
            return;
        }

        System.out.println("Adding event with timestamp: " + timestampMillis);
        events.add(timestampMillis);
        lastEvent = timestampMillis;

        //Prepare various timestamps
        long currentMillis = DateTimeUtils.currentTimeMillis(); //Need to use this method for testing
        Instant instant = Instant.ofEpochMilli(currentMillis);
        ZonedDateTime current = ZonedDateTime.ofInstant(instant, timezone);
        String tsSOD = DateParser.parseDate(current.withHour(0).withMinute(0).withSecond(0).withNano(0).toString());
        String tsSOM = DateParser.parseDate(current.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toString());
        String tsSOY = DateParser.parseDate(current.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0).toString());
        String tsEOD = DateParser.parseDate(current.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0).minusSeconds(1).toString());
        String tsEOM = DateParser.parseDate(current.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0).minusSeconds(1).toString());
        String tsEOY = DateParser.parseDate(current.plusYears(1).withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0).minusSeconds(1).toString());
        long tsSODl = DateParser.parseDateMills(tsSOD);
        long tsSOMl = DateParser.parseDateMills(tsSOM);
        long tsSOYl = DateParser.parseDateMills(tsSOY);
        long tsEODl = DateParser.parseDateMills(tsEOD);
        long tsEOMl = DateParser.parseDateMills(tsEOM);

        System.out.println("Events before filtering: " + events.size());

        events.removeIf(l -> l < tsSOYl); //Remove all events older than this year

        //Iterate over list and count
        long dayCounter = 0, monthCounter = 0, yearCounter = events.size();
        for(long l : events){
            if(l > tsSODl && l < tsEODl)
                dayCounter++;
            if(l > tsSOMl && l < tsEOMl)
                monthCounter++;
        }

        System.out.println("Values:" +
                "\tDayCounter: " + dayCounter +
                "\tMonthCounter: "+ monthCounter +
                "\tYearCounter: " + yearCounter);

        message.output("DayCounter", dayCounter);
        message.output("DayTimestamp", tsEOD);
        message.output("MonthCounter", monthCounter);
        message.output("MonthTimestamp", tsEOM);
        message.output("YearCounter", yearCounter);
        message.output("YearTimestamp", tsEOY);
    }

    @Override
    public void config(Message message) {
        message.addInput("value");
        message.addInput("timestamp");
    }
}
