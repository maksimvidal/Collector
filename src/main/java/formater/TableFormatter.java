package formater;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.softserve.logstat.collectors.Command;
import dnl.utils.text.table.TextTable;

public class TableFormatter implements Formatter {
	
	@Override
	public void draw(List<String> logs,Command command) {
		TextTable table=new TextTable(command.getParamArray(),transformList(logs));
		table.printTable();
	}
	
	protected void prepareList(List<String> list) {
		list.stream().forEach(x->x=x.substring(0, x.length()-1));
	}
	
	String[][] transformList(List<String> list){
		prepareList(list);
		String[][] data=new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			data[i]=list.get(i).split(";");
		}
		return data;
	}
}
