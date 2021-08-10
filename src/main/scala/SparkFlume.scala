import org.apache.spark._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
//import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming._
import org.apache.spark.storage.StorageLevel
import scala.io.Source
import scala.collection.mutable.HashMap
import java.io.File
import java.util.Date
import sys.process.stringSeqToProcess
import java.util.Calendar
import java.text.SimpleDateFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.flume._
import java.util.regex.Pattern
import java.util.regex.Matcher

object SparkFlume {


  def main(args: Array[String]) {

    // Configue Elasticseach connection
    val conf = new SparkConf()

    val sc = new SparkContext(conf) 

    sc.setLogLevel("ERROR")

    val ssc = new StreamingContext(sc, Seconds(5))
    val logs = FlumeUtils.createStream(ssc, "localhost",9090)


    //val regex = """([^ ]+ [ 0-9][0-9]? [0-9][0-9]:[0-9][0-9]:[0-9][0-9]) (\S+) (\S+:) (\S.*)$""".r
    val regex = """(\S+ \d+ \d+:\d+:\d+) (\S+) (\S+:) (\S.*)$""".r

    val p = Pattern.compile(regex.toString)

    logs.foreachRDD(log => {
      if (log.isEmpty) {
        println("No logs received in this time interval")
      }else{
	println("Logs received:")
	val array = log.collect()
	for(flumeEvent<-array){
	  val data = flumeEvent.event.getBody()
	  val logRecord = new String(data.array())
	  try{
    	    val m = p.matcher(logRecord)
            if (m.find) {
	      println("Match Found: "+m.group(1)+" "+m.group(2)+" "+m.group(3)+" "+m.group(4))
	    }else{
	      println("Match NOT Found: "+logRecord)
	    }
	  }catch{
	    case e: IllegalStateException => println("Match NOT Found: "+logRecord)
	  }
	}
      }
      
     }
    )

    ssc.start()
    ssc.awaitTermination()

  }
}

