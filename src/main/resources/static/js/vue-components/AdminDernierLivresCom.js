/**
 * Composant Vue.js pour afficher les derniers livres ajoutés
 */
const AdminDernierLivresCom = {
    template: `
        <div class="card dashboard-card h-100">
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Derniers livres ajoutés</h5>
                <a href="/dashboard/livres" class="btn btn-sm btn-primary">Voir tous</a>
            </div>
            <div class="card-body">
                <div v-if="loading" class="text-center py-3">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Chargement...</span>
                    </div>
                    <p class="text-muted mt-2">Chargement des livres...</p>
                </div>
                <div v-else-if="error" class="text-center py-3">
                    <i class="fas fa-exclamation-circle text-danger mb-3" style="font-size: 3rem;"></i>
                    <p class="text-danger">{{ error }}</p>
                </div>
                <div v-else-if="books.length === 0" class="text-center py-3">
                    <i class="fas fa-book-open text-muted mb-3" style="font-size: 3rem;"></i>
                    <p class="text-muted">Aucun livre n'a été ajouté récemment.</p>
                </div>
                <div v-else>
                    <a v-for="livre in books" :key="livre.id"  :href="'/dashboard/livres/' + livre.id+'/modifier'" >
                    <div class="recent-item recent-item-book">
                        <h6>{{ livre.titre }}</h6>
                        <div class="d-flex justify-content-between">
                            <p>{{ livre.auteur ? livre.auteur.nomComplet : 'Auteur inconnu' }}</p>
                            <p v-if="livre.dateCreation">{{ formatDate(livre.dateCreation) }}</p>
                        </div>
                    </div>
                        </a>

                </div>
            </div>
        </div>
    `,
    data() {
        return {
            books: [],
            loading: true,
            error: null
        };
    },
    mounted() {
        this.fetchBooks();
    },
    methods: {
        fetchBooks() {
            this.loading = true;
            this.error = null;
            
            fetch('/dashboard/recent-livres')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors du chargement des livres');
                    }
                    return response.json();
                })
                .then(data => {
                    this.books = data;
                    this.loading = false;
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    this.error = 'Impossible de charger les livres. Veuillez réessayer plus tard.';
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
