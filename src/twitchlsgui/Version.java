package twitchlsgui;

public class Version {

    private int major = 0;
    private int minor = 0;
    private int build = 0;
    private int revision = 0;

    public Version(int major, int minor, int build, int revision) {
	this.major = major;
	this.minor = minor;
	this.build = build;
	this.revision = revision;
    }

    public Version() {
	this.major = 0;
	this.minor = 0;
	this.build = 0;
	this.revision = 0;
    }

    public Version(String version) {
	parseVersion(version);
    }

    public String asString() {
	return "" + major + "." + minor + "." + build + "." + revision;
    }

    /**
     * Checks if the parameter is newer or not
     * 
     * @param version
     * @return
     */
    public boolean isNewerVersion(Version version) {
	if (this.major < version.major) {
	    return true;
	} else if (this.major >= version.major && this.minor < version.minor) {
	    return true;
	} else if (this.major >= version.major && this.minor >= version.minor
		&& this.build < version.build) {
	    return true;
	} else if (this.major >= version.major && this.minor >= version.minor
		&& this.build >= version.build
		&& this.revision < version.revision) {
	    return true;
	}
	return false;
    }

    private void parseVersion(String version) {
	String[] split = version.split("\\.");
	try {
	    this.major = Integer.parseInt(split[0]);
	    this.minor = Integer.parseInt(split[1]);
	    this.build = Integer.parseInt(split[2]);
	    this.revision = Integer.parseInt(split[3]);
	} catch (Exception e) {
	    if (Main_GUI._DEBUG)
		e.printStackTrace();
	}
    }
}
