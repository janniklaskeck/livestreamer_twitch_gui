package updateFramework;

import java.util.ArrayList;
import java.util.List;

import channel.Channel;
import settings.SettingManager;
import streamService.StreamService;

public class UpdateThread implements Runnable {

	public volatile boolean updateRunning = true;

	private List<Thread> workerList;

	public UpdateThread() {
		workerList = new ArrayList<Thread>();
	}

	@Override
	public void run() {
		while (SettingManager.getInstance().isAutoUpdate() && updateRunning) {
			update();
			try {
				Thread.sleep(SettingManager.getInstance().getCheckTimer() / 10);
			} catch (InterruptedException e) {
				updateRunning = false;
				e.printStackTrace();
			}
		}
	}

	private void update() {
		workerList.clear();
		for (StreamService ss : SettingManager.getInstance().getStreamServices()) {
			for (Channel c : ss.getChannels()) {
				UpdateWorker uw = new UpdateWorker(c);
				workerList.add(new Thread(uw));
			}
		}
		for (Thread t : workerList) {
			t.start();
		}

		for (Thread t : workerList) {
			try {
				t.join(SettingManager.getInstance().getTimeout());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
