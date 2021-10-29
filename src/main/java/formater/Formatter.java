package formater;

import java.util.List;

import com.softserve.logstat.collectors.Command;

public interface Formatter {
	void draw(List<String> logs, Command command);
}
