$(function(){
	$("#wizard").steps({
        headerTag: "h4",
        bodyTag: "section",
        transitionEffect: "fade",
        enableAllSteps: true,
        onStepChanging: function (event, currentIndex, newIndex) { 
            if ( newIndex === 1 ) {
                $('.wizard > .steps ul').addClass('step-2');
            } else {
                $('.wizard > .steps ul').removeClass('step-2');
            }
            if ( newIndex === 2 ) {
                $('.wizard > .steps ul').addClass('step-3');
            } else {
                $('.wizard > .steps ul').removeClass('step-3');
            }
            return true; 
        }, onFinished: function (event, currentIndex) {
            // Collect form data
            var formData = $("#movie_data").serialize();

            // Send an AJAX request to your Flask route
            $.ajax({
                type: 'POST',
                url: '/submit_form',
                data: formData,
                success: function (response) {
                    // Handle success response if needed
                    console.log("received data")
                    console.log(response);
                    if (response.message === "Movie data sent successfully") {
                        // Encode the array data as a query parameter
                        var dataArray = response.data;
                        var dataArrayParam = encodeURIComponent(JSON.stringify(dataArray));
            
                        // Redirect to the my_page URL with the data as a query parameter
                        window.location.href = '/my_page?data=' + dataArrayParam;
                    }
                    
                },
                error: function (error) {
                    // Handle error if needed
                    
                    console.error(error);
                }
            });
        },
        labels: {
            finish: "Submit",
            next: "Continue",
            previous: "Back"
        }
    });
    // Custom Button Jquery Steps
    $('.forward').click(function(){
    	$("#wizard").steps('next');
    })
    $('.backward').click(function(){
        $("#wizard").steps('previous');
    })
    // Date Picker
    var dp1 = $('#dp1').datepicker().data('datepicker');
    dp1.selectDate(new Date());
})
