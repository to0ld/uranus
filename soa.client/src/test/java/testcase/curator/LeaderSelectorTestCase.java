package testcase.curator;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class LeaderSelectorTestCase {
	private static final int CLIENT_QTY = 10;
	private static final String PATH = "/examples/leader";

	//
	@Test
	public void main() throws Exception {
		System.out.println("Create " + CLIENT_QTY + " clients, have each negotiate for leadership and then wait a random number of seconds before letting another leader election occur.");
		System.out.println("Notice that leader election is fair: all clients will become leader and will do so the same number of times.");

		List<CuratorFramework> clients = Lists.newArrayList();
		List<ExampleClient> examples = Lists.newArrayList();
		TestingServer server = new TestingServer();

		try {
			for (int i = 0; i < CLIENT_QTY; ++i) {
				CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
				clients.add(client);

				ExampleClient example = new ExampleClient(client, PATH, "Client #" + i);
				examples.add(example);

				client.start();
				example.start();
			}
			System.out.println("Press enter/return to quit\n");
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (Exception e) {
			Assert.fail(e.getMessage(), e);
		} finally {
			System.out.println("Shutting down...");

			for (ExampleClient exampleClient : examples) {
				CloseableUtils.closeQuietly(exampleClient);
			}
			for (CuratorFramework client : clients) {
				CloseableUtils.closeQuietly(client);
			}

			CloseableUtils.closeQuietly(server);
		}
	}

	public static class ExampleClient extends LeaderSelectorListenerAdapter implements Closeable {
		private final String name;
		private final LeaderSelector leaderSelector;
		private final AtomicInteger leaderCount = new AtomicInteger();

		public ExampleClient(CuratorFramework client, String path, String name) {
			this.name = name;

			// create a leader selector using the given path for management
			// all participants in a given leader selection must use the same
			// path
			// ExampleClient here is also a LeaderSelectorListener but this
			// isn't required
			leaderSelector = new LeaderSelector(client, path, this);

			// for most cases you will want your instance to requeue when it
			// relinquishes leadership
			leaderSelector.autoRequeue();
		}

		public void start() throws IOException {
			// the selection for this instance doesn't start until the leader
			// selector is started
			// leader selection is done in the background so this call to
			// leaderSelector.start() returns immediately
			leaderSelector.start();
		}

		@Override
		public void close() throws IOException {
			leaderSelector.close();
		}

		@Override
		public void takeLeadership(CuratorFramework client) throws Exception {
			// we are now the leader. This method should not return until we
			// want to relinquish leadership

			final int waitSeconds = (int) (5 * Math.random()) + 1;

			System.out.println(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
			System.out.println(name + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
			} catch (InterruptedException e) {
				System.err.println(name + " was interrupted.");
				Thread.currentThread().interrupt();
			} finally {
				System.out.println(name + " relinquishing leadership.\n");
			}
		}
	}
}
