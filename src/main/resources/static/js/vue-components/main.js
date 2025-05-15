/**
 * Fichier principal pour l'initialisation des composants Vue.js
 */
document.addEventListener('DOMContentLoaded', function() {
    if (typeof Vue === 'undefined') {
        console.error('Vue.js n\'est pas chargé');
        return;
    }
    
    if (document.getElementById('recent-books-container')) {
        if (typeof AdminDernierLivresCom === 'undefined') {
            console.error('AdminDernierLivresCom n\'est pas défini');
            return;
        }
        const booksApp = Vue.createApp({
            components: {
                'recent-books': AdminDernierLivresCom
            }
        });
        booksApp.mount('#recent-books-container');
    }
    
    if (document.getElementById('recent-authors-container')) {
        if (typeof AdminDernierAuteursCom === 'undefined') {
            console.error('AdminDernierAuteursCom n\'est pas défini');
            return;
        }
        const authorsApp = Vue.createApp({
            components: {
                'recent-authors': AdminDernierAuteursCom
            }
        });
        authorsApp.mount('#recent-authors-container');
    }
});
