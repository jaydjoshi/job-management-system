package com.dd.jobmangementsystem.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class ReadListener<T> implements ItemReadListener<T> {

	private final static Logger LOGGER =
			LoggerFactory.getLogger(ReadListener.class);

	@Override
	public void beforeRead() {
		LOGGER.info("ReaderListener::beforeRead()");
	}

	@Override
	public void afterRead(T item) {
		LOGGER.info("ReaderListener::afterRead() -> " + item);
	}

	@Override
	public void onReadError(Exception ex) {
		LOGGER.info("ReaderListener::onReadError() -> " + ex);
	}

}
