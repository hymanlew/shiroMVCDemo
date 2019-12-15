package moreway.redis;

import hyman.utils.Logutil;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.List;

/**
 * jedis操作redis的几种常见方式总结
 */
public class JedisDemo {

    /**
     * 普通同步方式，每次set之后都可以返回结果，标记是否成功。
     */
    public void jedisNormal() {
        Jedis jedis = new Jedis("localhost");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String result = jedis.set("n" + i, "n" + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Simple SET: " + ((end - start) / 1000.0) + " seconds");
        jedis.disconnect();
    }

    /**
     * 事务方式(Transactions)：
     * 特性：可以一次执行多个命令，本质是一组命令的集合。
     * 单独的隔离操作：一个事务中的所有命令都会被序列化，顺序地串行化执行而不会被其它命令插入，不许加塞。即事务执行过程中，不
     * 会被其他客户端发来的命令请求所打断。批量操作在发送 EXEC 命令前被放入队列缓存。
     * 
     * 没有隔离级别的概念：队列中的命令没有提交之前都不会实际的被执行，因为事务提交前任何指令都不被实际执行。也就不存在”事务内
     * 的查询要看到事务里的更新，在事务外查询不能看到“这个问题。
     * 
     * 不保证原子性：redis 同一个事务中如果有一条命令执行失败，其后的命令仍然会被执行，没有回滚。即部分支持事务。
     * 
     * redis 事务的作用是：一个队列中，一次性，顺序性，排他性的执行一系列命令。在事务执行过程，其他客户端提交的命令请求不会插
     * 入到事务执行命令序列中。
     * 
     * 一个事务从开始到执行会经历以下三个阶段：开始事务，命令入队，执行事务。
     * 它先以 MULTI 开始一个事务， 然后将多个命令入队到事务中， 最后由 EXEC 命令触发事务， 一并执行事务中的所有命令：
     * multi ， set hyman 'good man' ， get hyman ,  sadd set a b c d ， smembers set  ,  exec  执行如下：
     * 1) OK
     * 2) "good man"
     * 3) (integer) 4
     * 4) 1) "b"
     * 2) "a"
     * 3) "d"
     * 4) "c"
     * 
     * 全体连坐：收到 EXEC 命令后进入事务执行，一旦事务中任意命令执行失败，则其余的命令都将失败。
     * 冤头债主：执行 EXEC 命令进入事务执行后，一条命令执行失败，不影响其余命令的正常执行。
     * 两者的区别是：前者是在命令入队时就已经报错 error（就像 java 中的检查性异常），根本就到不了执行阶段，所以一旦执行就会直
     * 接被取消。而后者是命令正常（就像 java 中的非检查性异常，如 NPE），只有在执行过程中才会抛出异常，所以在异常之前或之后的
     * 命令执行不受影响。
     * 
     * 单个 Redis 命令的执行是原子性的，但 Redis 没有在事务上增加任何维持原子性的机制，所以 Redis 事务的执行并不是原子性的，
     * 即 redis 对事务是部分支持的，而不是像关系型数据库那样强一致性，要么全成功要么全失败。
     * 事务可以理解为一个打包的批量执行脚本，但批量指令并非原子化的操作，中间某条指令的失败不会导致前面已做指令的回滚，也不会
     * 造成后续的指令不做。
     * 
     * multi，标记一个事务块的开始。
     * exec，执行所有事务块内的命令。
     * discard，取消事务，放弃执行事务块内的所有命令。
     * watch key1 key2。。。，监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。
     * unwatch，取消 WATCH 命令对所有 key 的监视。
     * 
     * watch 监控：
     * 在数据处理中，一致性与并发性总是会有冲突的。
     * 表锁：对数据是否会被修改很悲观，所以干脆把整张表给锁住。并发性极差，但是一致性极好。
     * 行锁：对数据是否会被修改很乐观，所以只把一行数据锁住。锁的范围就相对小多了，也保证了并发性。
     * 悲观锁：就类似于表锁，所以每次操作数据时都会上锁，其他人只能排队等着，即操作之前先上锁，多应用在关系型数据库中。
     * 乐观锁：是在操作的当前行末尾加上 version 标记，原理就像 SVN commit的原理。在更新数据时会先判断在此期间别人有没有在操作
     * 它，然后使用版本号的机制进行上锁。它是工作中用的最多的，适用于多读的应用类型，这样可以提高吞吐量。
     * CAS（check and set）：检查后再设置。
     * 
     * 先监控然后再开启事务进行操作，如果在开始监控后，有其他人已经修改了数据，则自己的操作执行时就会失败，即事务执行失败。
     * 
     * watch 指令类似于乐观锁，事务提交时如果 key 的值已被别的客户端改变，则整个事务队列都不会执行。通过 watch 命令在事务执行
     * 之前监控了多个 keys，若在 watch 之后有任何 key 的值发生了变化，则 exec执行的事务都将被放弃，同时返回 nullmulti-bulk
     * 应答以通知调用者事务执行失败。
     * 
     * 如果没有人修改在事务成功执行后，就要 unwatch，以便于其他人进行修改。
     * 并且一旦执行了 exec后，则之前加的监控锁都会被取消掉。
     */
    public void jedisTrans() {
        Jedis jedis = new Jedis("localhost");
        long start = System.currentTimeMillis();
        Transaction tx = jedis.multi();
        for (int i = 0; i < 100000; i++) {
            tx.set("t" + i, "t" + i);
        }
        List<Object> results = tx.exec();
        long end = System.currentTimeMillis();
        System.out.println("Transaction SET: " + ((end - start) / 1000.0) + " seconds");
        jedis.disconnect();
    }

    /**
     * 管道(Pipelining)：
     * 管道是一种两个进程之间单向通信的机制。有时我们需要采用异步的方式，一次发送多个指令，并且不同步等待其返回结果。这样可以取得非常好的执行效率。
     */
    public void jedisPipelined() {
        Jedis jedis = new Jedis("localhost");
        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("p" + i, "p" + i);
        }
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined SET: " + ((end - start)/1000.0) + " seconds");
        jedis.disconnect();
    }

    /**
     * 管道中调用事务：
     * 在某种需求下，我们需要异步执行命令，但是又希望多个命令是有连续的，所以我们就采用管道加事务的调用方式。jedis是支持在管道
     * 中调用事务的。
     * 效率上可能会有所欠缺。
     */
    public void jedisCombPipelineTrans() {
        Jedis jedis = new Jedis("localhost");
        long start = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        pipeline.multi();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("" + i, "" + i);
        }
        pipeline.exec();
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined transaction: " + ((end - start)/1000.0) + " seconds");
        jedis.disconnect();
    }

    /**
     * 分布式直连同步调用：
     * 这个是分布式直接连接，并且是同步调用，每步执行都返回执行结果。类似地，还有异步管道调用。其实就是分片。
     */
    public void jedisShardNormal() {
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("localhost",6379),
                new JedisShardInfo("localhost",6380));

        ShardedJedis sharding = new ShardedJedis(shards);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String result = sharding.set("sn" + i, "n" + i);
            Logutil.logger.info(result);
        }
        long end = System.currentTimeMillis();
        System.out.println("Simple@Sharing SET: " + ((end - start)/1000.0) + " seconds");
        sharding.disconnect();
    }

    /**
     * 分布式直连异步调用
     */
    public void jedisShardpipelined() {
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("localhost",6379),
                new JedisShardInfo("localhost",6380));

        ShardedJedis sharding = new ShardedJedis(shards);

        ShardedJedisPipeline pipeline = sharding.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("sp" + i, "p" + i);
        }
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined@Sharing SET: " + ((end - start)/1000.0) + " seconds");
        sharding.disconnect();
    }

    /**
     * 分布式连接池同步调用：
     * 如果分布式调用代码是运行在线程中，那么上面两个直连调用方式就不合适了，因为直连方式是非线程安全的，这时就必须选择连接池调用。
     * 连接池的调用方式，适合大规模的redis集群，并且多客户端的操作。
     */
    public void jedisShardSimplePool() {
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("localhost",6379),
                new JedisShardInfo("localhost",6380));

        ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
        ShardedJedis one = pool.getResource();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String result = one.set("spn" + i, "n" + i);
        }
        long end = System.currentTimeMillis();
        pool.close();
        System.out.println("Simple@Pool SET: " + ((end - start)/1000.0) + " seconds");
        pool.destroy();
    }

    /**
     * 分布式连接池异步调用
     */
    public void jedisShardPipelinedPool() {
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("localhost",6379),
                new JedisShardInfo("localhost",6380));

        ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
        ShardedJedis one = pool.getResource();
        ShardedJedisPipeline pipeline = one.pipelined();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("sppn" + i, "n" + i);
        }
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        pool.close();
        System.out.println("Pipelined@Pool SET: " + ((end - start)/1000.0) + " seconds");
        pool.destroy();
    }

    /**
     * 需要注意的地方:
     * 事务和管道都是异步模式。在事务和管道中不能同步查询结果。比如下面两个调用，都是不允许的。
     *
     * 事务和管道都是异步的，个人感觉，在管道中再进行事务调用，没有必要，不如直接进行事务模式。
     * 分布式中，连接池的性能比直连的性能略好(见后续测试部分)。
     * 分布式调用中不支持事务。因为事务是在服务器端实现，而在分布式中，每批次的调用对象都可能访问不同的机器，所以没法进行事务。
     *
     * 总结：
     * 分布式中，连接池方式调用不但线程安全外，根据上面的测试数据，也可以看出连接池比直连的效率更好。经测试分布式中用到的机器越多，调用会越慢。
     */
    public void notallow(){

        Jedis jedis = new Jedis("localhost");
        Transaction tx = jedis.multi();
        for (int i = 0; i < 100000; i++) {
            tx.set("t" + i, "t" + i);
        }
        //不允许
        System.out.println(tx.get("t1000").get());
        List<Object> results = tx.exec();

        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("p" + i, "p" + i);
        }
        //不允许
        System.out.println(pipeline.get("p1000").get());
        results = pipeline.syncAndReturnAll();
    }

}
