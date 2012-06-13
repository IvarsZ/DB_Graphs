package mySqlGraph;

public class MySqlConnectionParameters {
	
	private String url;
	private String user;
	private String password;
	
	
	public MySqlConnectionParameters(String url, String user, String password) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
	}

	protected String getUrl() {
		return url;
	}


	protected String getUser() {
		return user;
	}


	protected String getPassword() {
		return password;
	}
}
