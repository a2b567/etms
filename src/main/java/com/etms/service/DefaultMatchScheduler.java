package com.etms.service;

import com.etms.model.Match;
import java.time.LocalDateTime;
import java.util.List;

public class DefaultMatchScheduler implements MatchScheduler {

    @Override
    public List<Match> scheduleMatches(List<Match> matches) {
        // Assign time slots – simple logic: start from now + 1 hour per match
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        for (Match m : matches) {
            if ("SCHEDULED".equals(m.getStatus()) && m.getScheduledTime() == null) {
                m.setScheduledTime(time.toString());
                time = time.plusHours(1);
            }
        }
        return matches;
    }
}