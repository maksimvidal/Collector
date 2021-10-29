package com.softserve.logstat.collectors;

import java.util.List;

import com.softserve.logstat.model.Log;

public interface Collector {

	List<String> execute(Command command,List<Log> logs);
}
