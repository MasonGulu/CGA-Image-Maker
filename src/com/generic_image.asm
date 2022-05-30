org 0x100
cpu 8086

; Parameters
%define DELAYTIMEH      1
                ; 1 = 0xffff iterations
                ; 10 = ~8 seconds
                ; 1 = 0.8 seconds
%define IMAGETYPE       4
                ; Image type
                ; 0 unchanged
                ; 1 320x200 palette 0
                ; 2 320x200 palette 1
                ; 6 320x200 mode 5
                ; 3 640x200
                ; 4 80 x100 composite
                ; 5 80 x25 text
%define INTENSITY       0
                ; 0b010000 for intensity bit set
%define IMAGEMODE       2
                ; Mode
                ; 0 delay and then exit, resetting video
                ; 1 delay and then exit, don't reset video
                ; 2 wait for keyboard, reset video
                ; 3 wait for keyboard, don't reset video
%if IMAGETYPE = 5
    %define IMAGESIZE       4000
%else
    %define IMAGESIZE       16192
%endif
%define IMAGESTART      0


; CONSTANTS
%define CGA_REG_SELECT  0x3D4
%define CGA_REG_CONTENT 0x3D5
%define CGA_MODE        0x3D8
%define CGA_COLOR       0x3D9

; Macros
%macro writetoport 2
    ; Expects (port, data) port is a word, data is a byte
    ; Registers are NOT preserved.
    mov dx, %1
    mov al, %2
    out dx, al
%endmacro

%macro writetocga 2
    ; Expects (register, data)
    writetoport CGA_REG_SELECT,  %1
    writetoport CGA_REG_CONTENT, %2
%endmacro

entry:
    mov ax, 0xb800
    push es
    mov es, ax
                ; AX contains the address of DATA_imagetype

%if IMAGETYPE = 1
    mov ax, 0x0004
    int 0x10    ; Make the bios set the video mode to 320x200 color

    writetoport CGA_COLOR, INTENSITY
    ; Set color palette to 0

%elif IMAGETYPE = 2
    mov ax, 0x0004
    int 0x10    ; Make the bios set the video mode to 320x200 color

    writetoport CGA_COLOR, (0b100000 | INTENSITY)
    ; Set color palette to 1
%elif IMAGETYPE = 6
    mov ax, 0x0005
    int 0x10    ; Make the bios set the video mode to 320x200 with the alternative palette

    writetoport CGA_COLOR, INTENSITY

%elif IMAGETYPE = 3
    mov ax, 0x0006
    int 0x10    ; Make the bios set the video mode to 640x200

%elif IMAGETYPE = 4
    writetoport CGA_MODE, 0b000001
    ; Set mode to 80x25 and disable video signal

    writetocga 0x04, 0x7f
    ; Set vertical line total to 127

    writetocga 0x06, 0x64
    ; Set vertical displayed character rows to 100

    writetocga 0x07, 0x70
    ; Set vertical scan position to 112

    writetocga 0x09, 0x01
    ; Set character scan line count to 1
    ; Code adapted from https://github.com/drwonky/cgax16demo/blob/master/CGA16DMO.CPP

    writetoport CGA_MODE, 0b001001
    ; Enable video again
%elif IMAGETYPE = 5
    writetoport CGA_MODE, 9
    ; Set high res 80x25 color text mode, with 16 colors instead of blinking
%endif

load_image:
    ; Copy image into memory
    mov cx, IMAGESIZE
                ; CX tells rep how many times to repeat.
    mov si, DATA_image
                ; Source offset DS:SI
    mov di, IMAGESTART
                ; Starting offset in CGA ram ES:DI
    pushf
    cld         ; Make sure movsb increments SI and DI
    rep movsb   ; Copy!
    popf

%if IMAGEMODE <= 1
    mov ax, DELAYTIMEH
    delay:
        cmp ax, 0
        je exit_delay
        call SUB_delay
        sub ax, 1
        jmp delay
    exit_delay:
    mov al, IMAGEMODE
    cmp al, 1
    je exit
    jmp reset_video
%else
    mov ax, 0
    int 0x16    ; Wait for keyboard
    mov al, IMAGEMODE
    cmp al, 3
    je exit     ; Don't reset video
%endif

reset_video:
    mov ax, 0x0003
    int 0x10    ; Change video mode
exit:
    pop es
    mov ah, 0x4C
    mov al, 0
    int 0x21    ; Terminate program DOS
    retf        ; Terminate program BASIC

%if IMAGEMODE <= 1
; Only if the image is in a delay mode
SUB_delay:
    push ax
    mov ax, 0xffff
    delay_loop:
        cmp ax, 0
        je delay_finish
        sub ax, 1
        jmp delay_loop
    delay_finish:
        pop ax
        ret
%endif

DATA_image: