package redis.command.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Result {
    private final List<List<Map<String, Map<String, List<String>>>>> result;
    private List<Map<String, Map<String, List<String>>>> listNames;
    private Map<String, Map<String, List<String>>> mapNames;
    private Map<String, List<String>> mapIds;

    public Result(List<List<Map<String, Map<String, List<String>>>>> result,
                  List<Map<String, Map<String, List<String>>>> listNames,
                  Map<String, Map<String, List<String>>> mapNames, Map<String, List<String>> mapIds) {
        this.result = result;
        this.listNames = listNames;
        this.mapNames = mapNames;
        this.mapIds = mapIds;
    }

    public void setMapNames(Map<String, Map<String, List<String>>> mapNames) {
        this.mapNames = mapNames;
    }

    public void setListNames(List<Map<String, Map<String, List<String>>>> listNames) {
        this.listNames = listNames;
    }

    public void setMapIds(Map<String, List<String>> mapIds) {
        this.mapIds = mapIds;
    }

    public List<List<Map<String, Map<String, List<String>>>>> result() {
        return result;
    }

    public List<Map<String, Map<String, List<String>>>> listNames() {
        return listNames;
    }

    public Map<String, Map<String, List<String>>> mapNames() {
        return mapNames;
    }

    public Map<String, List<String>> mapIds() {
        return mapIds;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Result) obj;
        return Objects.equals(this.result, that.result) &&
                Objects.equals(this.listNames, that.listNames) &&
                Objects.equals(this.mapNames, that.mapNames) &&
                Objects.equals(this.mapIds, that.mapIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, listNames, mapNames, mapIds);
    }

    @Override
    public String toString() {
        return "Result[" +
                "result=" + result + ", " +
                "listNames=" + listNames + ", " +
                "mapNames=" + mapNames + ", " +
                "mapIds=" + mapIds + ']';
    }

}
