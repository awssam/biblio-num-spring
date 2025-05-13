/**
 * Composant Vue.js pour afficher les derniers auteurs ajoutés
 */
const RecentAuthorsComponent = {
    template: `
        <div class="card dashboard-card h-100">
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Derniers auteurs ajoutés</h5>
                <a href="/dashboard/auteurs" class="btn btn-sm btn-success">Voir tous</a>
            </div>
            <div class="card-body">
                <div v-if="loading" class="text-center py-3">
                    <div class="spinner-border text-success" role="status">
                        <span class="visually-hidden">Chargement...</span>
                    </div>
                    <p class="text-muted mt-2">Chargement des auteurs...</p>
                </div>
                <div v-else-if="error" class="text-center py-3">
                    <i class="fas fa-exclamation-circle text-danger mb-3" style="font-size: 3rem;"></i>
                    <p class="text-danger">{{ error }}</p>
                </div>
                <div v-else-if="authors.length === 0" class="text-center py-3">
                    <i class="fas fa-user-edit text-muted mb-3" style="font-size: 3rem;"></i>
                    <p class="text-muted">Aucun auteur n'a été ajouté récemment.</p>
                </div>
                <div v-else>
                <a  v-for="auteur in authors" :key="auteur.id" :href="'/dashboard/auteurs/' + auteur.id+'/modifier'" class="">
                    <div class="recent-item recent-item-author">
                        <h6>{{ auteur.nomComplet }}</h6>
                        <div class="d-flex justify-content-between">
                            <p>{{ auteur.nationalite || 'Nationalité non spécifiée' }}</p>
                            <p v-if="auteur.dateCreation">{{ formatDate(auteur.dateCreation) }}</p>
                        </div>
                    </div>
                        </a>

                </div>
            </div>
        </div>
    `,
    data() {
        return {
            authors: [],
            loading: true,
            error: null
        };
    },
    mounted() {
        this.fetchAuthors();
    },
    methods: {
        fetchAuthors() {
            this.loading = true;
            this.error = null;
            
            fetch('/dashboard/recent-auteurs')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors du chargement des auteurs');
                    }
                    return response.json();
                })
                .then(data => {
                    this.authors = data;
                    this.loading = false;
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    this.error = 'Impossible de charger les auteurs. Veuillez réessayer plus tard.';
                    this.loading = false;
                });
        },
        formatDate(dateString) {
            if (!dateString) return '';
            
            const options = { day: '2-digit', month: '2-digit', year: 'numeric' };
            return new Date(dateString).toLocaleDateString('fr-FR', options);
        }
    }
};
