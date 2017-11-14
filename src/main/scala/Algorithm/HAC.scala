package Algorithm

import java.io.{File, PrintWriter}

import data.SongInfo
import org.apache.spark.rdd.RDD
//author: Jiangtao
object HAC {

  def kCluster = 3

  def runHAC(songInfos: RDD[SongInfo], symbol: Symbol): Unit = {
    var filteredSI = songInfos.filter(si => si.isValid(symbol))
    var finalCluster = hierarchicalAgglomerative(filteredSI, symbol)
    symbol match {
      case 'fuzzyLoudness => printResult(finalCluster,"output/all-hac-fuzzyLoudness.csv",symbol)
      case 'fuzzyLength => printResult(finalCluster,"output/all-hac-fuzzyLength.csv",symbol)
      case 'fuzzyTempo => printResult(finalCluster,"output/all-hac-fuzzyTempo.csv",symbol)
      case 'fuzzyHotness => printResult(finalCluster,"output/all-hac-fuzzyHotness.csv",symbol)
      case 'combinedHotness => printResult(finalCluster,"output/all-hac-combinedHotness.csv",symbol)
    }

  }

  // implementation of HAC algorithm
  def hierarchicalAgglomerative(songInfos: RDD[SongInfo], symbol: Symbol): Array[List[SongInfo]] = {
    var clusters = songInfos.sortBy(x => x.getCoordinate(symbol))
      .map(List(_)).collect()
      .zipWithIndex

    while (clusters.length > kCluster) {

      var pairs = clusters.zip(clusters.tail)
      var minDistancePair = pairs.reduceLeft((a,b) =>
        if (a._1._1.head.calculateDistance(a._2._1.head,symbol) < b._1._1.head.calculateDistance(b._2._1.head,symbol) ) a
        else b)

      clusters = clusters.filter(_._2 != minDistancePair._1._2)
        .map(x => {if (x._2 == minDistancePair._2._2) (minDistancePair._1._1 ::: minDistancePair._2._1,x._2 )
        else x})
    }

    return clusters.map(_._1)
  }

  // print the result to files
  def printResult(result: Array[List[SongInfo]], path: String, symbol: Symbol): Unit = {
    val pw = new PrintWriter(new File(path))
    var count = 0
    for (a <- result){
      a.foreach(s => pw.println(count + "," + s.getSymbol(symbol)))
      count = count + 1
    }
    pw.close()
  }

}
