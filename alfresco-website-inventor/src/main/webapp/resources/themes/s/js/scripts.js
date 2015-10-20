var filter;
var clearFilter;

$(document).ready(function(){

    filter = function(element) {
        var value = $(element).val().toLowerCase();

        var links = $('#links > a');
        if (value === '') {
            links.show();
        }
        else {
            var linksToHide  = links.filter( function(){
                return $(this).text().toLowerCase().indexOf(value) == -1;
            });
            var linksToShow = links.filter( function(){
                return $(this).text().toLowerCase().indexOf(value) > -1;
            });

            linksToHide.hide();
            linksToShow.show();
        }
    }

    clearFilter = function(element) {
        $(element).val('');
        $('#links > a').show();
    }
});
