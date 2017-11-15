

import scala.util.Try
//author: Jiangtao
class SongInfo (track : String, du : String, ld : String, sh : String, ah : String, tp: String) extends Serializable{
  var TRACK_ID : String = track
  var DURATION : String = du
  var LOUDNESS : String = ld
  var SONG_HOTNESS : String = sh
  var ARTIST_HOT : String = ah
  var TEMPO : String = tp



  def this(line : String) ={
    this("","","","","","")
    def tokens = line.split(";")
    this.TRACK_ID = tokens(0)
    this.TRACK_ID = tokens(16)
    this.DURATION = tokens(5)
    this LOUDNESS = tokens(6)
    this SONG_HOTNESS = tokens(25)
    this ARTIST_HOT = tokens(20)
    this TEMPO = tokens(7)
  }



  def this(value : Double, symbol: Symbol) ={
    this("","","","","","")
    symbol match {
      case 'fuzzyLoudness => this.LOUDNESS = value.toString
      case 'fuzzyLength => this.DURATION = value.toString
      case 'fuzzyTempo => this.TEMPO = value.toString
      case 'fuzzyHotness => this.SONG_HOTNESS = value.toString
    }
  }

  def this(songHot : Double, artistHot : Double, symbol: Symbol) ={
    this("","","","","","")
    symbol match {
      case 'combinedHotness => {
        this.SONG_HOTNESS = songHot.toString
        this.ARTIST_HOT = artistHot.toString}
    }
  }





  def calculateDistance(sf : SongInfo, symbol: Symbol): Double = {
    symbol match{
      case 'fuzzyLoudness => get1DimensionDistance(sf.LOUDNESS.toDouble, this.LOUDNESS.toDouble)
      case 'fuzzyLength => get1DimensionDistance(sf.DURATION.toDouble, this.DURATION.toDouble)
      case 'fuzzyTempo => get1DimensionDistance(sf.TEMPO.toDouble, this.TEMPO.toDouble)
      case 'fuzzyHotness => get1DimensionDistance(sf.SONG_HOTNESS.toDouble, this.SONG_HOTNESS.toDouble)
      case 'combinedHotness => get2DimensionDistance(sf.SONG_HOTNESS.toDouble, sf.ARTIST_HOT.toDouble)
      case _ => 0.0
    }
  }

  def get1DimensionDistance(p1 : Double, p2 : Double) = math.abs(p1 - p2)

  def get2DimensionDistance(p1 : Double, p2 : Double) = math.pow(math.pow(p1 - this.SONG_HOTNESS.toDouble, 2) + math.pow(p2 - this.ARTIST_HOT.toDouble, 2) ,0.5)

  def getSymbol(symbol: Symbol): String = {
    symbol match{
      case 'fuzzyLoudness => this.LOUDNESS
      case 'fuzzyLength => this.DURATION
      case 'fuzzyTempo => this.TEMPO
      case 'fuzzyHotness => this.SONG_HOTNESS
      case 'combinedHotness => this.SONG_HOTNESS + "," + this.ARTIST_HOT
      case _ => ""
    }
  }


  def getCoordinate(symbol: Symbol): Double = {
    symbol match{
      case 'fuzzyLoudness => this.LOUDNESS.toDouble
      case 'fuzzyLength => this.DURATION.toDouble
      case 'fuzzyTempo => this.TEMPO.toDouble
      case 'fuzzyHotness => this.SONG_HOTNESS.toDouble
      case 'combinedHotness =>  math.pow( math.pow(this.SONG_HOTNESS.toDouble,2) + math.pow(this.ARTIST_HOT.toDouble,2),0.5)
      case _ => 0.0
    }
  }

  def isValid(symbol: Symbol) : Boolean = {
    symbol match{
      case 'fuzzyLoudness => Try(this.LOUDNESS.toDouble).isSuccess
      case 'fuzzyLength => Try(this.DURATION.toDouble).isSuccess
      case 'fuzzyTempo => Try(this.TEMPO.toDouble).isSuccess
      case 'fuzzyHotness => Try(this.SONG_HOTNESS.toDouble).isSuccess && this.SONG_HOTNESS.toDouble != 0
      case 'combinedHotness =>
        Try(this.SONG_HOTNESS.toDouble).isSuccess &&
        Try(this.ARTIST_HOT.toDouble).isSuccess &&
        this.SONG_HOTNESS.toDouble != 0 &&
        this.ARTIST_HOT.toDouble != 0
      case _ => false
    }
  }

}

