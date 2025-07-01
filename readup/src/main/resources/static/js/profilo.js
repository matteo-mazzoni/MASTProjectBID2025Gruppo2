document.addEventListener('DOMContentLoaded', function() {
    // Show the edit profile modal if the flag is set
    if (window.showEditProfileModalFlag) {
        var myModal = new bootstrap.Modal(document.getElementById('editProfileModal'));
        myModal.show();
    }

    // Auto-submit form when a new image is selected
    const imageUploadInput = document.getElementById('imageUpload');
    if (imageUploadInput) {
        imageUploadInput.addEventListener('change', function() {
            this.form.submit();
        });
    }
});
