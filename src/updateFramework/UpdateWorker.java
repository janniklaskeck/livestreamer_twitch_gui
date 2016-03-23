package updateFramework;

import app.channel.Channel;
import javafx.concurrent.Task;

public class UpdateWorker extends Task<Void> {

	private Channel toUpdate;

	public UpdateWorker(Channel c) {
		toUpdate = c;
	}

	public Channel getToUpdate() {
		return toUpdate;
	}

	@Override
	protected Void call() throws Exception {
		toUpdate.updateChannel();
		return null;
	}

}
