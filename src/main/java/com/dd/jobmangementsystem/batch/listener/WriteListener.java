package com.dd.jobmangementsystem.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WriteListener<S> implements ItemWriteListener<S> {

	private final static Logger LOGGER =
			LoggerFactory.getLogger(WriteListener.class);

	@Override
	public void beforeWrite(List<? extends S> items) {
		LOGGER.info("ReaderListener::beforeWrite() -> " + items);
	}

	@Override
	public void afterWrite(List<? extends S> items) {
		LOGGER.info("ReaderListener::afterWrite() -> " + items);
	}

	@Override
	public void onWriteError(Exception exception, List<? extends S> items) {
		LOGGER.info("ReaderListener::onWriteError() -> " + exception + ", " + items);
	}

}
