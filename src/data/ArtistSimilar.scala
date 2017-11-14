package data
//author: Jiangtao
class ArtistSimilar(id : String, similar : String) extends Serializable{
  var ARTIST_ID : String = id
  var SIMILAR_ARTIST : String = similar



  def this(line : String) ={
    this("","")
    def tokens = line.split(";")
    this.ARTIST_ID = tokens(0)
    this.SIMILAR_ARTIST = tokens(1)
  }


}

