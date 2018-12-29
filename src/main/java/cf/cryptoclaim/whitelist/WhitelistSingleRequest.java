package cf.cryptoclaim.whitelist;

import java.util.Set;

public class WhitelistSingleRequest {
	private String path;
	private Set<String> methods;
	private Set<String> parameters;
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setMethods(Set<String> methods) {
		this.methods = methods;
	}
	
	public void setParameters(Set<String> parameters) {
		this.parameters = parameters;
	}
	
	public String getPath() {
		return path;
	}
	
	public Set<String> getMethods() {
		return methods;
	}
	
	public Set<String> getParameters() {
		return parameters;
	}
	
}
