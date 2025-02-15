document.addEventListener("DOMContentLoaded", () => {
    // Récupère tous les liens du menu latéral
    const menuLinks = document.querySelectorAll(".sidebar nav ul li a");

    // Ajoute un écouteur d'événement pour chaque lien
    menuLinks.forEach(link => {
        link.addEventListener("click", (event) => {
            // Si le lien est celui de déconnexion, laisse-le faire son travail
            if (link.classList.contains("logout")) {
                return;  // Laisse le comportement de redirection par défaut
            }
            event.preventDefault();
  
            // Supprime la classe active de tous les liens
            menuLinks.forEach(link => link.classList.remove("active"));
  
            // Ajoute la classe active au lien cliqué
            link.classList.add("active");
  
            // Récupère la cible de la section à afficher
            const targetId = link.getAttribute("data-target");
  
            // Masque toutes les sections
            const sections = document.querySelectorAll(".section");
            sections.forEach(section => section.classList.add("hidden"));
  
            // Affiche uniquement la section cible
            const targetSection = document.getElementById(targetId);
            if (targetSection) {
                targetSection.classList.remove("hidden");
            }
        });
    });

    // Affiche la première section par défaut
    if (menuLinks.length > 0) {
        menuLinks[0].click();
    }
});