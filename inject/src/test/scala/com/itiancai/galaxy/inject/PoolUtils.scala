package com.itiancai.galaxy.inject

import java.util.concurrent.Executors

import com.twitter.concurrent.NamedPoolThreadFactory
import com.twitter.util.ExecutorServiceFuturePool

object PoolUtils {

  def newUnboundedPool(name: String): ExecutorServiceFuturePool = {
    new ExecutorServiceFuturePool(
      Executors.newCachedThreadPool(
        new NamedPoolThreadFactory(name)))
  }

  def newFixedPool(name: String, size: Int = 1): ExecutorServiceFuturePool = {
    new ExecutorServiceFuturePool(
      Executors.newFixedThreadPool(size,
        new NamedPoolThreadFactory(name)))
  }
}
