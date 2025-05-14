/**
 * Composant Vue.js pour afficher des suggestions de livres similaires
 * à celui actuellement consulté par l'utilisateur
 */
export default {
    props: {
        livreId: {
            type: Number,
            required: true
        },
        nombreSuggestions: {
            type: Number,
            default: 4
        },
        categoriePrioritaire: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            livresSuggeres: [],
            chargement: true,
            erreur: null
        }
    },
    mounted() {
        this.chargerSuggestions();
    },
    methods: {
        chargerSuggestions() {
            this.chargement = true;
            this.erreur = null;
            
            fetch(`/livres/${this.livreId}/suggestions?nombre=${this.nombreSuggestions}&categorie=${this.categoriePrioritaire}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Problème lors de la récupération des suggestions');
                    }
                    return response.json();
                })
                .then(data => {
                    this.livresSuggeres = data;
                    this.chargement = false;
                })
                .catch(error => {
                    this.erreur = error.message;
                    this.chargement = false;
                    console.error('Erreur lors du chargement des suggestions:', error);
                });
        }
    },
    template: `
        <div class="suggestions-container">
            <h3 class="mb-3">Vous pourriez aussi aimer</h3>
            
            <div v-if="chargement" class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Chargement...</span>
                </div>
                <p class="mt-2">Recherche de suggestions...</p>
            </div>
            
            <div v-else-if="erreur" class="alert alert-danger">
                {{ erreur }}
            </div>
            
            <div v-else-if="livresSuggeres.length === 0" class="alert alert-info">
                Aucune suggestion disponible pour le moment.
            </div>
            
            <div v-else class="row">
                <div v-for="livre in livresSuggeres" :key="livre.id" class="col-md-3 mb-4">
                    <div class="card h-100 book-card">
                        <div class="position-relative">
                            <img v-if="livre.couverture" :src="livre.couverture" 
                                class="card-img-top" alt="Couverture du livre" style="height: 200px; object-fit: cover;">
                            <div v-else class="text-center p-4 bg-light">
                                <i class="fa-solid fa-book text-muted" style="font-size: 3rem;"></i>
                            </div>
                            
                            <div v-if="livre.copies_disponibles > 0" class="badge bg-success position-absolute top-0 end-0 m-2">
                                Disponible
                            </div>
                            <div v-else class="badge bg-danger position-absolute top-0 end-0 m-2">
                                Indisponible
                            </div>
                        </div>
                        
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">{{ livre.titre }}</h5>
                            <p class="card-text text-muted">
                                <small>
                                    {{ livre.auteurs_display }}
                                </small>
                            </p>
                            <div class="mt-auto">
                                <a :href="'/livres/' + livre.id" class="btn btn-outline-primary btn-sm w-100">
                                    <i class="fa-solid fa-chevron-right me-1"></i>
                                    Voir le détail
                                    <i class="fa-solid fa-chevron-right ms-1"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
}