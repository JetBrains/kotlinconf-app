import libs.*
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.CoreData.*

class FavoritesManager {
    fun isFavorite(session: KSession) = getFavorite(session) != null

    private fun getFavorite(session: KSession): KFavorite? {
        val moc = appDelegate.managedObjectContext

        val request = NSFetchRequest(entityName = "KFavorite")
        request.fetchLimit = 1
        request.predicate = NSPredicate.predicateWithFormat(
                "sessionId == %@", 
                argumentArray = nsArrayOf(session.id!!.toNSString()))

        return attempt(null) {
            moc.executeFetchRequest(request).firstObject?.uncheckedCast<KFavorite>()
        }
    }

    fun toggleFavorite(
        session: KSession,
        onComplete: (Boolean) -> Unit
    ) {
        val newFavorite = !isFavorite(session)
        val service = KonfService({ log(it.localizedDescription) })

        setLocalFavorite(session, isFavorite = newFavorite)
        onComplete(newFavorite)

        val uuid = appDelegate.userUuid
        if (newFavorite) {
            service.addFavorite(session, uuid, onComplete = {})
        } else {
            service.deleteFavorite(session, uuid, onComplete = {})
        }
    }

    private fun setLocalFavorite(session: KSession, isFavorite: Boolean) {
        val moc = appDelegate.managedObjectContext

        val favorite = getFavorite(session)
        if (favorite != null && !isFavorite) {
            moc.deleteObject(favorite)
        } else if (isFavorite) {
            val favoriteItem = NSEntityDescription
                    .insertNewObjectForEntityForName("KFavorite", inManagedObjectContext = moc)
                    .uncheckedCast<KFavorite>()

            favoriteItem.sessionId = session.id
        }

        moc.save()
    }

    fun getFavoriteItemIds(): NSArray {
        val moc = appDelegate.managedObjectContext

        var favorites: List<KFavorite> = emptyList()
        attempt(null) {
            favorites = moc.executeFetchRequest(NSFetchRequest(entityName = "KFavorite")).toList<KFavorite>()
        }

        val idsArray = NSMutableArray.arrayWithCapacity(favorites.size.toLong())
        for (favoriteItem in favorites) {
            idsArray.addObject(favoriteItem.sessionId!!.toNSString())
        }

        return idsArray
    }
}
