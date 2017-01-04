package com.thefuntasty.infinity.test.adapter;

import org.junit.Rule;

import io.github.plastix.rxschedulerrule.RxSchedulerRule;

public class BaseRxAdapterTest extends BaseAdapterTest {
	@Rule public RxSchedulerRule rxSchedulerRule = new RxSchedulerRule();
}
