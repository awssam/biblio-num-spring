/**
 * Fichier principal pour l'initialisation des composants Vue.js
 */
document.addEventListener('DOMContentLoaded', function() {
    if (typeof Vue === 'undefined') {
        console.error('Vue.js n\'est pas chargé');
        return;
    }
    
    if (document.getElementById('recent-books-container')) {

        if (typeof RecentBooksComponent === 'undefined') {
            console.error('RecentBooksComponent n\'est pas défini');
            return;
        }
        new Vue({
            el: '#recent-books-container',
            components: {
                'recent-books': RecentBooksComponent
            }
        });
    }
    
    if (document.getElementById('recent-authors-container')) {
        if (typeof RecentAuthorsComponent === 'undefined') {
            console.error('RecentAuthorsComponent n\'est pas défini');
            return;
        }
        new Vue({
            el: '#recent-authors-container',
            components: {
                'recent-authors': RecentAuthorsComponent
            }
        });
    }
});
