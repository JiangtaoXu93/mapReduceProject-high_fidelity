package Algorithm

import java.io.{File, PrintWriter}

import data.SongInfo
import org.apache.spark.rdd.RDD
//author: Jiangtao

object Kmeans {

  def runKmeans(songInfos : RDD[SongInfo],centroids: Seq[SongInfo], symbol: Symbol): Unit = {
    var filteredSI = songInfos.filter(si => si.isValid(symbol))
    var finalCentroid = kMeans(filteredSI,centroids,symbol)
    var finalClusters = getClusterByCentroids(filteredSI,finalCentroid,symbol)
    symbol match {
      case 'fuzzyLoudness => printResult(finalClusters,"output/all-kmeans-fuzzyLoudness.csv",symbol)
      case 'fuzzyLength => printResult(finalClusters,"output/all-kmeans-fuzzyLength.csv",symbol)
      case 'fuzzyTempo => printResult(finalClusters,"output/all-kmeans-fuzzyTempo.csv",symbol)
      case 'fuzzyHotness => printResult(finalClusters,"output/all-kmeans-fuzzyHotness.csv",symbol)
      case 'combinedHotness => printResult(finalClusters,"output/all-kmeans-combinedHotness.csv",symbol)
    }

  }


  def kMeans(songInfos : RDD[SongInfo],intitCentroids: Seq[SongInfo], symbol: Symbol): Seq[SongInfo] = {
    var centroids = intitCentroids
    for(i <- 0 to 9){
      // calculate cluster by input centroids
      var clusters = getClusterByCentroids(songInfos,centroids, symbol)
      // recalculate centroids
      centroids = getCentroids(clusters, symbol)
    }

    return centroids
  }


  def getClusterByCentroids(songInfos :RDD[SongInfo],centroids: Seq[SongInfo],symbol: Symbol ) = {
    songInfos.groupBy(song => {
      centroids.reduceLeft((a, b) =>
        if ((song.calculateDistance(a, symbol) ) < (song.calculateDistance(b, symbol))) a
        else b).getSymbol(symbol)})
  }

  def getCentroids(clusters : RDD[(String, Iterable[SongInfo])], symbol: Symbol ) : Seq[SongInfo]= {
    symbol match {
      case 'combinedHotness => get2DimensionCentroids(clusters, symbol)
      case _ => get1DimensionCentroids(clusters, symbol)
    }
  }

  def get1DimensionCentroids(clusters : RDD[(String, Iterable[SongInfo])], symbol: Symbol ) : Seq[SongInfo]= {
    val centroids = clusters.map(key => {
      var sum = 0.0
      var it = key._2
      for (i <- it){
        sum = i.getSymbol(symbol).toDouble + sum
      }
      new SongInfo(sum/it.size, symbol)
    }).collect().toList

    return centroids
  }

  def get2DimensionCentroids(clusters : RDD[(String, Iterable[SongInfo])], symbol: Symbol ) : Seq[SongInfo]= {
    val centroids = clusters.map(key => {
      var songSum = 0.0
      var artistSum = 0.0
      var it = key._2
      for (i <- it){
        songSum = i.SONG_HOTNESS.toDouble + songSum
        artistSum = i.ARTIST_HOT.toDouble + artistSum
      }
      new SongInfo(songSum/it.size, artistSum/it.size, symbol)
    }).collect().toList

    return centroids
  }

  // print the result to files
  def printResult(result: RDD[(String, Iterable[SongInfo])], path: String, symbol: Symbol): Unit = {
    val pw = new PrintWriter(new File(path))
    result.collect().foreach(x => x._2.foreach(
      y => pw.println(x._1 + "," +y.getSymbol(symbol))))
    pw.close()
  }





}

