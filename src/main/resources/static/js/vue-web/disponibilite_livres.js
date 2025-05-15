const { ref, onMounted } = Vue

export default {
  props: {
    livreId: {
      type: Number,
      required: true
    }
  },
  setup(props) {
    // États réactifs pour stocker les données récupérées de l'API
    const exemplairesRestants = ref(0)
    const copiesDisponibles = ref(0)
    const userAuthenticated = ref(false)
    const limiteAtteinte = ref(false)
    const messageLimite = ref('')
    const datePublication = ref(null)
    const loading = ref(true)
    const error = ref(null)

    // Fonction pour récupérer les données de disponibilité
    const fetchDisponibilite = async () => {
      loading.value = true
      error.value = null
      
      try {
        const response = await fetch(`/livres/${props.livreId}/disponibilite_json`)
        
        if (!response.ok) {
          throw new Error(`Erreur HTTP: ${response.status}`)
        }
        
        const data = await response.json()
        
        // Mettre à jour les états avec les données reçues
        exemplairesRestants.value = data.exemplaires_restants
        copiesDisponibles.value = data.copies_disponibles
        userAuthenticated.value = data.user_authenticated
        limiteAtteinte.value = data.limite_atteinte
        messageLimite.value = data.message_limite
        datePublication.value = data.date_publication
      } catch (err) {
        error.value = `Erreur lors de la récupération des données: ${err.message}`
        console.error(error.value)
      } finally {
        loading.value = false
      }
    }
    
    // Charger les données au montage du composant
    onMounted(() => {
      fetchDisponibilite()
    })
    
    return { 
      exemplairesRestants,
      copiesDisponibles,
      userAuthenticated,
      limiteAtteinte,
      messageLimite,
      datePublication,
      loading,
      error
    }
  },
  template: `
    <div class="disponibilite-container">
      <div v-if="loading" class="text-center py-3">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
        <p class="mt-2">Chargement des informations de disponibilité...</p>
      </div>
      
      <div v-else-if="error" class="alert alert-danger" role="alert">
        {{ error }}
      </div>
      
      <div v-else>
        <p><strong>Copies disponibles :</strong> {{ exemplairesRestants }} / {{ copiesDisponibles }}</p>
        
        <div v-if="datePublication">
          <p><strong>Date de publication :</strong> {{ datePublication }}</p>
        </div>

        <div v-if="exemplairesRestants > 0 && userAuthenticated && !limiteAtteinte" class="mt-3">
          <a :href="'/emprunter/' + livreId" class="btn btn-success">
            <i class="fa-solid fa-book-reader"></i> Emprunter ce livre
          </a>
        </div>
        
        <div v-else-if="userAuthenticated && limiteAtteinte" class="alert alert-warning mt-3">
          <i class="fa-solid fa-exclamation-triangle"></i> {{ messageLimite }}
        </div>
        
        <div v-else-if="!userAuthenticated" class="text-warning mt-3">
          <i class="fa-solid fa-info-circle"></i> 
          Connectez-vous pour emprunter ce livre. 
          <a href="/connexion/" class="btn btn-outline-primary btn-sm ml-2">Se connecter</a>
        </div>
        
        <div v-else class="text-danger mt-3">
          <i class="fa-solid fa-exclamation-circle"></i> 
          Désolé, ce livre n'est pas disponible pour le moment.
        </div>
      </div>
    </div>
  `
}