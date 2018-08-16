/* Copyright 2015 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.predic8.membrane.core.interceptor.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class LazyRateLimit extends RateLimitStrategy {

	private DateTime nextCleanup = new DateTime();
	public ConcurrentHashMap<String, AtomicInteger> requestCounterFromIP = new ConcurrentHashMap<String, AtomicInteger>();

	public LazyRateLimit(Duration requestLimitDuration, int requestLimit) {
		this.requestLimitDuration = requestLimitDuration;
		this.requestLimit = requestLimit;
		incrementNextCleanupTime();
	}

	@Override
	public boolean isRequestLimitReached(String ip) {
		synchronized (nextCleanup) {
			if (DateTime.now().isAfter(nextCleanup)) {
				for (AtomicInteger info : requestCounterFromIP.values()) {
					info.set(0);
				}
				incrementNextCleanupTime();
			}
		}
		addRequestEntry(ip);
		return requestCounterFromIP.get(ip).get() > requestLimit;
	}

	private void addRequestEntry(String addr) {
		synchronized (requestCounterFromIP) {
			if (!requestCounterFromIP.containsKey(addr)) {
				requestCounterFromIP.put(addr, new AtomicInteger());
			}
		}
		requestCounterFromIP.get(addr).incrementAndGet();
	}

	private void incrementNextCleanupTime() {
		nextCleanup = DateTime.now().plus(requestLimitDuration);
	}

	@Override
	public DateTime getServiceAvailableAgainTime(String ip) {
		return nextCleanup;
	}

	@Override
	public void updateAfterConfigChange() {
		for (AtomicInteger info : requestCounterFromIP.values()) {
			info.set(0);
		}
		incrementNextCleanupTime();
	}

}
