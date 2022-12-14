$.fn.animateRotate = function(angleFron, angleTo, duration, easing, complete) {
    var args = $.speed(duration, easing, complete);
    var step = args.step;
    return this.each(function(i, e) {
        args.step = function(now) {
            $.style(e, 'transform', 'rotate(' + now + 'deg)');
            if (step) return step.apply(this, arguments);
        };

    	$({deg: angleFron}).animate({deg: angleTo}, args);
    });
};

jQuery(function($) {

	$( '.centered' ).each(function( e ) {
		$(this).css('margin-top',  ($('#main-slider').height() - $(this).height())/2);
	});

	$(window).resize(function(){
		$( '.centered' ).each(function( e ) {
			$(this).css('margin-top',  ($('#main-slider').height() - $(this).height())/2);
		});
	});

	//goto top
	$('.gototop').click(function(event) {
		event.preventDefault();
		$('html, body').animate({
			scrollTop: $("body").offset().top
		}, 500);
	});	
});

function configurarMascaras() {

	$('.mask-date').mask('00/00/0000', {clearIfNotMatch: true});
	$('.mask-time').mask('00:00', {clearIfNotMatch: true});
	$('.mask-cep').mask('00000-000', {clearIfNotMatch: true});
	$('.mask-cpf').mask('000.000.000-00', {clearIfNotMatch: true});
	$('.mask-cnpj').mask('00.000.000/0000-00', {clearIfNotMatch: true});
	$('.mask-money').mask("####################0.00", {reverse: true, clearIfNotMatch: true});
	$('.mask-percent').mask('##0,00%', {reverse: true, clearIfNotMatch: true});

	$('.mask-number').each(function() {

		var thiz = $(this);
		var maxlength = thiz.attr('maxlength');
		var maskNum = '000000000000000';

		if(maxlength && maxlength < 15) {
			maskNum = maskNum.substring(0, maxlength);
		}

		thiz.mask(maskNum);
	});
	
	$('.mask-placa').mask('ZZZ-0000', {
		clearIfNotMatch: true,
		translation: {
		      'Z': {
		        pattern: /[a-zA-Z]/
		      }
		    }
	});

	var phoneMaskBehavior = function (val) {
		return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
	},
	phoneOptions = {
		onKeyPress: function(val, e, field, options) {
			field.mask(phoneMaskBehavior.apply({}, arguments), options);
		},
		clearIfNotMatch: true
	};
	$('.mask-phone').mask(phoneMaskBehavior, phoneOptions);

	$('.mask-cpf-cnpj').each(function() {

		var val = $(this).val();
		var mask = val ? (val.length >= 14 ? '00.000.000/0000-00' : '000.000.000-000') : '00.000.000/0000-00';
		$(this).mask(mask, {
			clearIfNotMatch: true,
			onKeyPress: keyPressCpfCnpj
		});
	});
}

function keyPressCpfCnpj(val, e, field, options) {
	
	if(val.length > 14) {
		field.mask('00.000.000/0000-00', {onKeyPress: keyPressCpfCnpj, clearIfNotMatch: true});
	} else {
		field.mask('000.000.000-000', {onKeyPress: keyPressCpfCnpj, clearIfNotMatch: val.length < 14});
	}
}

function openModal(modalClass, btnFocusId) {
	
	$('.' + modalClass).modal();
	if(btnFocusId) {
		var btnFocus = document.getElementById(btnFocusId);
		if(btnFocus) {
			btnFocus.focus();
		}
	}
}

function closeModal(modalClass) {
	
	$('.' + modalClass).modal('hide');
}

var slideUp = false;

function prepareToSlideUp() {
	slideUp = true;
}

function slide(panelId, imgId){
	
	if(slideUp) {
		hideSlideUp(panelId, imgId);
	}
}

function hideSlideUp(panelId, imgId){
	
	var panel = $('#' + panelId);
	
	panel.slideUp(300);
	
	var img = $('#' + imgId);
	img.animateRotate(180, 360, 300);
}

function showSlideDown(panelId, imgId){
	
	$('#' + panelId).slideDown(300);
	
	var img = $('#' + imgId);
	img.animateRotate(360, 180, 300);
}

function configurarDatePicker() {
	
	var parent = $('.mask-date').parent();
	parent.addClass('date');
	parent.datepicker({
		todayHighlight: true,
		autoclose: true,
		inline: false,
		format: 'dd/mm/yyyy',
		language: 'pt-BR'
	});
}

function bloquearCampos(podeEditar) {

	if(!podeEditar) {
		var url = window.location.href;
		var contemEdit = url.indexOf("?") != -1;
		if(contemEdit) {
			$("input[type='text'],select", "#content-pg-id").attr('disabled', 'disabled');
			$("button[type='submit'],select", "#content-pg-id").attr('disabled', 'disabled');
		}
	}
}

function hideAlertInfo() {
	$('.alert-info').slideUp(300);
}

function hideMessage(timeout) {
	if($('.bf-messages').length > 0) {
		setTimeout(hideAlertInfo, timeout);
	}
}

function carregarCep() {

	//Quando o campo cep perde o foco.
	$("#input_form-usuario\\:cep").blur(function() {

		//Nova variável "cep" somente com dígitos.
		var cep = $(this).val().replace(/\D/g, '');

		//Verifica se campo cep possui valor informado.
		if (cep != "") {

			//Expressão regular para validar o CEP.
			var validacep = /^[0-9]{8}$/;

			//Valida o formato do CEP.
			if(validacep.test(cep)) {

				//Preenche os campos com "..." enquanto consulta webservice.
				$("#input_form-usuario\\:rua").val("...");
				$("#input_form-usuario\\:bairro").val("...");
				$("#input_form-usuario\\:cidade").val("...");
				$("#input_form-usuario\\:estado").val("...");
				$("#input_form-usuario\\:ibge").val("...");

				//Consulta o webservice viacep.com.br/
				$.getJSON("https://viacep.com.br/ws/"+ cep +"/json/?callback=?", function(dados) {

					if (!("erro" in dados)) {

						//Atualiza os campos com os valores da consulta.
						$("#input_form-usuario\\:rua").val(dados.logradouro);
						$("#input_form-usuario\\:bairro").val(dados.bairro);
						$("#input_form-usuario\\:cidade").val(dados.localidade);
						$("#input_form-usuario\\:estado").val(dados.uf);
						$("#input_form-usuario\\:ibge").val(dados.ibge);
					} //end if.
					else {
						//CEP pesquisado não foi encontrado.
						limpa_formulario_cep();
						alert("Digite um CEP existente!");
					}
				});
			} //end if.
			else {
				//cep é inválido.
				limpa_formulario_cep();
				alert("Formato de CEP inexistente!");
			}
		} //end if.
		else {
			//cep sem valor, limpa formulário.
			limpa_formulario_cep();
		}
	});
}

function limpa_formulario_cep() {
	// Limpa valores do formulário de cep.
	$("#input_form-usuario\\:rua").val("");
	$("#input_form-usuario\\:bairro").val("");
	$("#input_form-usuario\\:cidade").val("");
	$("#input_form-usuario\\:estado").val("");
	$("#input_form-usuario\\:ibge").val("");
}