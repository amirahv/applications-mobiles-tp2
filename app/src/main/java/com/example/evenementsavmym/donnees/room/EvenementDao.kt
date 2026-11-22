package com.example.evenementsavmym.donnees.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.evenementsavmym.modele.Evenement
import kotlinx.coroutines.flow.Flow

@Dao
interface EvenementDao {

    // Observe la liste, triée par date puis par heure.
    @Query("""
        SELECT * FROM evenements
        ORDER BY dateLocale ASC, heureLocale ASC, nom ASC, id ASC
    """)
    fun observerTous(): Flow<List<Evenement>>

    // Retrouve l'évènement à afficher dans la fiche de détail.
    @Query("SELECT * FROM evenements WHERE id = :idEvenement")
    fun observerParId(idEvenement: String): Flow<Evenement?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insererTous(evenements: List<Evenement>)

    @Query("DELETE FROM evenements")
    suspend fun supprimerTous()

    // Lit une fois la liste sauvegardée pour préparer une annulation.
    @Query("SELECT * FROM evenements")
    suspend fun obtenirTous(): List<Evenement>

    // Remplace les anciens résultats par ceux du rafraîchissement.
    @Transaction
    suspend fun remplacerTous(evenements: List<Evenement>) {
        supprimerTous()
        insererTous(evenements)
    }
}