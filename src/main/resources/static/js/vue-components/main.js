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
        new Vue({
            el: '#recent-books-container',
            components: {
                'recent-books': AdminDernierLivresCom
            }
        });
    }
    
    if (document.getElementById('recent-authors-container')) {
        if (typeof AdminDernierAuteursCom === 'undefined') {
            console.error('AdminDernierAuteursCom n\'est pas défini');
            return;
        }
        new Vue({
            el: '#recent-authors-container',
            components: {
                'recent-authors': AdminDernierAuteursCom
            }
        });
    }
});
