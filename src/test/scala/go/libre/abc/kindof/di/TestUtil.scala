package go.libre.abc.kindof.di

object TestUtil {
  def clean(): Unit = {
    cache.clear()
  }
}