package data

//author: Jiangtao
class ArtistTerm(id : String, term : String) extends Serializable{
  var ARTIST_ID : String = id
  var ARTIST_TERM : String = term



  def this(line : String) ={
    this("","")
    def tokens = line.split(";")
    this.ARTIST_ID = tokens(0)
    this.ARTIST_TERM = tokens(1)
  }


}

