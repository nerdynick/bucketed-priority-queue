package com.nerdynick;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.google.common.util.concurrent.ForwardingBlockingQueue;

/**
 * Wrapper classes around a given {@see BlockingQueue}. The BlockingQueue is
 * supplied from the given {@see Supplier}.
 * 
 * @author Nikoleta Verbeck
 */
public class Bucket<K, E> extends ForwardingBlockingQueue<E> {
	private final BlockingQueue<E> _queue;
	private final BucketSensor _sensor;
	public final K key;

	public Bucket(final Supplier<BlockingQueue<E>> queueSupplier, final BiFunction<K, Bucket<K, E>, BucketSensor> bucketSensor, final K key) {
		this.key = key;
		this._queue = queueSupplier.get();
		this._sensor = bucketSensor.apply(key, this);
	}

	@Override
	protected BlockingQueue<E> delegate() {
		return _queue;
	}

	@Override
	public boolean offer(E o) {
		this._sensor.onOffer();
		return super.offer(o);
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		this._sensor.onOffer();
		return super.offer(e, timeout, unit);
	}

	@Override
	public boolean add(E o) {
		this._sensor.onOffer();
		return super.add(o);
	}

	@Override
	public void put(E e) throws InterruptedException {
		this._sensor.onOffer();
		super.put(e);
	}

	@Override
	public boolean remove(Object o) {
		this._sensor.onTake();
		return super.remove(o);
	}

	@Override
	public E remove() {
		this._sensor.onTake();
		return super.remove();
	}

	@Override
	public E poll() {
		this._sensor.onTake();
		return super.poll();
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		this._sensor.onTake();
		return super.poll(timeout, unit);
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		throw new RuntimeException("Not Implemented");
	}

	public boolean isReady() {
		return this._sensor.isReady();
	}

}