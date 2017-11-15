import java.io.{File, PrintWriter}
import HAC.runHAC
import Kmeans.runKmeans


import org.apache.spark.{SparkConf, SparkContext}
//author: Jiangtao
object Driver {
  def kCluster = 3

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Million Classification")
    val sc = new SparkContext(conf)
    val songInput = sc.textFile("input/MillionSongSubset/song_info.csv")
    val songInfos = songInput.mapPartitionsWithIndex { (idx, iterate) => if (idx == 0) iterate.drop(1) else iterate }.map(line => new SongInfo(line))
    val centroids = new Array[SongInfo](kCluster)

    for (i <- 0 until kCluster) {
      //generate random intial centroids
      centroids(i) = songInfos.takeSample(false, 1)(0)
      if (!centroids(i).isValid('fuzzyLoudness)) centroids(i).LOUDNESS = (0.123 * i + 0.01).toString
      if (!centroids(i).isValid('fuzzyLength)) centroids(i).DURATION = (0.123 * i + 0.01).toString
      if (!centroids(i).isValid('fuzzyTempo)) centroids(i).TEMPO = (0.123 * i + 0.01).toString
      if (!centroids(i).isValid('fuzzyHotness)) centroids(i).SONG_HOTNESS = (0.123 * i + 0.01).toString
      if (!centroids(i).isValid('combinedHotness)) {
        centroids(i).SONG_HOTNESS = (0.123 * i + 0.01).toString
        centroids(i).SONG_HOTNESS = (0.321 * i + 0.01).toString
      }
    }


    //runHAC(songInfos, 'fuzzyLoudness)
//    runHAC(songInfos, 'fuzzyLength)
//    runHAC(songInfos, 'fuzzyTempo)
//    runHAC(songInfos, 'fuzzyHotness)
//    runHAC(songInfos, 'combinedHotness)

    runKmeans(songInfos, centroids, 'fuzzyLoudness)
//    runKmeans(songInfos, centroids, 'fuzzyLength)
//    runKmeans(songInfos, centroids, 'fuzzyTempo)
//    runKmeans(songInfos, centroids, 'fuzzyHotness)
//    runKmeans(songInfos, centroids, 'combinedHotness)

  }

}
