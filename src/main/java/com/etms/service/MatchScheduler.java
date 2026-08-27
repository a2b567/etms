package com.etms.service;

import com.etms.model.Match;
import java.util.List;

public interface MatchScheduler {
    List<Match> scheduleMatches(List<Match> matches);
}