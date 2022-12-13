package chen.you.flows

/**
 *  author: you : 2021/12/7
 */
interface Repository {

    /**
     * 获取Api 层service
     * @param serviceClass
     * @param <T>
     * @return
     */
    fun <T> getService(serviceClass: Class<T>): T


    //fun <T : RoomDatabase> getRoomDatabase(databaseClass: Class<T>, dbName: String): T

}