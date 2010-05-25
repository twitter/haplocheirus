package com.twitter.haplocheirus

import com.twitter.gizzard.Future
import com.twitter.gizzard.nameserver.NameServer
import com.twitter.gizzard.scheduler.{JobScheduler, PrioritizingJobScheduler}
import org.specs.Specification
import org.specs.mock.{ClassMocker, JMocker}


object TimelineStoreServiceSpec extends Specification with JMocker with ClassMocker {
  "TimelineStoreService" should {
    val nameServer = mock[NameServer[HaplocheirusShard]]
    val scheduler = mock[PrioritizingJobScheduler]
    val jobScheduler = mock[JobScheduler]
    val future = mock[Future]
    val replicationFuture = mock[Future]
    val shard1 = mock[HaplocheirusShard]
    val shard2 = mock[HaplocheirusShard]
    var service: TimelineStoreService = null

    doBefore {
      service = new TimelineStoreService(nameServer, scheduler, Jobs.RedisCopyFactory, future, replicationFuture)
    }

    "append" in {
      val data = "hello".getBytes
      val timelines = List("t1", "t2")

      expect {
        one(nameServer).findCurrentForwarding(0, 632754681242344982L) willReturn shard1
        one(nameServer).findCurrentForwarding(0, 632753581730716771L) willReturn shard2
        one(shard1).append(data, "t1")
        one(shard2).append(data, "t2")
      }

      service.append(data, timelines)
    }

    "remove" in {
      val data = "hello".getBytes
      val timelines = List("t1", "t2")

      expect {
        one(nameServer).findCurrentForwarding(0, 632754681242344982L) willReturn shard1
        one(nameServer).findCurrentForwarding(0, 632753581730716771L) willReturn shard2
        one(shard1).remove(data, "t1")
        one(shard2).remove(data, "t2")
      }

      service.remove(data, timelines)
    }
  }
}
